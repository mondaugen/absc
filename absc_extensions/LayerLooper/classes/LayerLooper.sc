LayerLooper {
	// TODO: More economical allocation of buffers using a single spare
	// buffer that is given when needed
	// Requires record and play group so that the play and record synths can be put at
	// proper points in the signal chain.

	// TODO: Why doesn't \gain get set properly?
	// Why is there doubling when we record on a playing loop?
	var <>maxNumLayers, <>curNumLayers, <>buffers, <>playingLoops,
	<>tempoClock, <>loopLength, <>inBus, <>outBus, <>server,
	<>maxBufferLength, <>playingSynths, <>synthGains, <>playRates, <>recordGroup,
	<>playGroup, <>recording;

	*new { |server, inBus, outBus, recordGroup, playGroup,
		tempoClock, numLayers = 8, loopLength = 8,
		maxBufferLength = 60|

		^super.new.init(server,inBus,outBus,recordGroup,playGroup,
			tempoClock,numLayers,loopLength,maxBufferLength);
	}

	init { |server, inBus, outBus, recordGroup, playGroup,
		tempoClock, numLayers, loopLength,
		maxBufferLength|

		// Send synth defs
		// Synth for recording
		SynthDef(\l_recorder,{|in,buf,len|
			RecordBuf.ar(In.ar(in) * EnvGen.ar(Env([0,1,1,0],
				[0,len,0]),doneAction:2),buf);
		}).send(server);

		// Synth for playing
		SynthDef(\l_player,{|out, buf, len, gain = 0,
			attack=0.02, decay=0.02, rate=1.0|
			Out.ar(out,
				PlayBuf.ar(1,buf,rate)
				* EnvGen.ar(
					Env([0,1,1,0],[attack,len - attack - decay,decay],
						\welch), doneAction:2) * gain.dbamp);
		}).send(server);

		this.server = server;
		this.inBus  = inBus;
		this.outBus = outBus;
		this.recordGroup = recordGroup;
		this.playGroup = playGroup;
		this.loopLength = loopLength;
		this.tempoClock = tempoClock;
		this.maxNumLayers = numLayers;
		this.curNumLayers = 0;
		this.playingLoops = Array.fill(numLayers,{nil});
		this.maxBufferLength = maxBufferLength;
		this.buffers = Array.fill(numLayers,{
			Buffer.alloc(this.server,this.server.sampleRate
				* (this.maxBufferLength));
		});
		this.playingSynths = Array.fill(numLayers,{nil});
		this.playRates = Array.fill(numLayers,{1.0});
		this.synthGains = Array.fill(numLayers,{0});
		this.recording = Array.fill(numLayers,{false});

	}

	recordLoop { |loopIndex|
		if(this.recording[loopIndex] == false,{
			var lenSecs = this.tempoClock.beats2secs(this.loopLength) -
			this.tempoClock.beats2secs(0);
			var bundle = this.server.makeBundle(false,{
				this.hardKillLoop(loopIndex);
				this.recording[loopIndex] = true;
				"recording".postln;
				Synth.after(this.recordGroup,\l_recorder,[\in,this.inBus,
					\buf,this.buffers[loopIndex],
					\len,lenSecs]);
			});
			this.server.makeBundle(nil,{
				var buf = this.buffers[loopIndex];
				this.playingLoops[loopIndex] = [
					this.loopLength,{
						if(this.playingLoops[loopIndex].notNil,{
							"playing".postln;
							this.recording[loopIndex] = false;
							this.server.makeBundle(nil,{
								this.playingSynths[loopIndex] = Synth.before(this.playGroup,
									\l_player,
									[\rate,this.playRates[loopIndex],
										\out,this.outBus,
										\buf,buf,
										\gain,this.synthGains[loopIndex],
										\len,lenSecs],
									this.server);
							});
							// Return time so it gets rescheduled
							this.playingLoops[loopIndex][0];
							},{
								// Return nil so it doesn't get rescheduled
								nil;
						});
				}];
				this.tempoClock.sched(this.loopLength,
					this.playingLoops[loopIndex][1]);
			},bundle);
		});

	}

	hardKillLoop { |loopIndex|
		this.playingLoops[loopIndex] = nil;
		if(this.playingSynths[loopIndex].notNil,{
			this.playingSynths[loopIndex].free;
			this.playingSynths[loopIndex] = nil;
		});
	}

	killLoop { |loopIndex| this.playingLoops[loopIndex] = nil; }

	setLayerGain { |layer,gain|
		this.playingSynths[layer].set(\gain,gain);
		this.synthGains[layer] = gain;
	}

	setLayerRate { |layer,rate|
		this.playingSynths[layer].set(\rate,rate);
		this.playRates[layer] = rate;
	}

	free {
		this.playingSynths.do({|synth| synth.free});
		this.buffers.do({|buf| buf.free});
		this.super.free;
	}
}

	