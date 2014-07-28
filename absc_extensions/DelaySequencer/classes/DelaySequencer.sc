DelaySequencer {
	var <>inbus, <>outBus, <>sequenceEvents, <>recordLength, <>recordPeriod, <>buffers, <>bufferFreedom,
	<>target, <>addAction, <>server, <>attack, <>decay, <>tempoClock, <>maxRecordLength;

	*new { arg server, inBus, outBus, numBuffers, maxRecordLength, sequenceLength, tempoClock,
		target, addAction='addToHead';
		^super.new.init(server,inBus,outBus,numBuffers,maxRecordLength,
			sequenceLength,tempoClock,target,addAction);
	}

	// Sends synth defs. If you want to be sure they have been compiled, wrap the calling of this function
	// in a fork and call server.sync after calling DelaySequencer.new(..) and before doing anything else
	init { arg server, inBus, outBus, numBuffers, maxRecordLength, sequenceLength, tempoClock,
		target, addAction;
		this.server = server;
		this.target = target;
		this.addAction = addAction;
		this.maxRecordLength = maxRecordLength;
		this.buffers = Array.fill(numBuffers,Buffer.alloc(server, maxRecordLength * server.sampleRate));
		this.bufferFreedom = Array.fill(numBuffers,{true});
		this.recordLength = maxRecordLength;
		this.recordPeriod = 1;
		this.tempoClock = tempoClock;

		SynthDef("temp_rec",{ |buf, lenSecs|
			var input =  SoundIn.ar(0) * EnvGen.kr(Env.new([0,1,1,0],[0,lenSecs,0]),doneAction:2);
			RecordBuf.ar(input,buf,doneAction:2);
		}).send(this.server);

		SynthDef("simple_play",{ |buf,rate=1,lenSecs=1,attack=0.1,decay=0.1,gain=0|
			var output = PlayBuf.ar(1,buf,rate) * EnvGen.ar(Env.new([0,1,1,0] * gain.dbamp,
				[attack,lenSecs - attack - decay,decay],'sine'),doneAction:2);
			Out.ar(0,output ! 2);
		}).send(this.server);

		this.sequenceEvents = Array.fill(sequenceLength,{(delay: 0, rate: 1, gain: -inf, length: 0)});
	}

	// Given delay and play length, determine the total time until the event will be over
	getTotalEventLength{ |delay,playLength|
		delay + playLength;
	}

	getNextFreeBufIndex {
		var index = 0, returnVal = nil;
		while({this.bufferFreedom[index].not},{ index = index + 1; });
		if(index < this.buffers.size,{returnVal = index});
		returnVal;
	}

	play {
		var bufIndex = this.getNextFreeBufIndex;
		var buf = this.buffers[bufIndex];
		var bufLengthSecs;
		var maxPlayLength = 0;
		this.bufferFreedom[bufIndex] = false;
		if(buf.notNil,{
			var recSynth = Synth(\temp_rec,[\buf,buf,
				\lenSecs,this.recordLength],this.target,this.addAction);
			bufLengthSecs = buf.numFrames / server.sampleRate;
			this.sequenceEvents.do({|event,index|
				var localLength = event.length;
				var eventPlayLength;
				if(localLength > (bufLengthSecs / event.rate),{localLength = bufLengthSecs / event.rate});
				this.tempoClock.sched(event.delay,{
					Synth(\simple_play,[\buf,buf,\rate,event.rate,\lenSecs,localLength,
						\attack,this.attack,\decay,this.decay,\gain,event.gain],this.target,this.addAction);
				});
				eventPlayLength = this.getTotalEventLength(event.delay,event.length);
				if(eventPlayLength > maxPlayLength,{maxPlayLength = eventPlayLength});
			});
			this.tempoClock.sched(maxPlayLength,{this.bufferFreedom[bufIndex] = true;});
		});
	}


	sched { |deltaTime|
		this.tempoClock.sched(deltaTime,{this.play; this.recordPeriod});
	}

}