SlowerVoice {
	var <>buffers, <>in, <>out, <>recordingSynths, <>playingSynths, <>masterVolume, <>group, <>busyBufs, <>server, <>rampTime, <>noteOnFunc, <>noteOffFunc;

	*new {
		// polyphony is maximum number of voices
		// in is where it records from
		// out is where it plays to
		// bufferLength is the length of each record buffer
		// in seconds
		// a
		arg server, polyphony = 12, in = 0, out = 0,
		bufferLength = 30;
		^super.new.init(server,polyphony,in,out,bufferLength);
	}

	init { arg server, polyphony, in, out, bufferLength;

		this.in = in;
		this.out = out;
		this.server = server;

		// allocate group
		// TODO: You can bend all the notes currently held, but when they are
		// retriggered, there is no bend, is that what you want?
		this.group = Group.new(this.server);

		this.masterVolume = Synth.basicNew(
			\sv_masterVolume,server);

		// allocate buffers

		this.buffers = Array.fill(polyphony,
			{Buffer.alloc(this.server,
				this.server.sampleRate * bufferLength, 1)});

		// send synthdefs

		SynthDef(\sv_recbuf,{
			arg out = 0, bufnum = 0, in = 0, gate = 0;
			var soundin;
			soundin = SoundIn.ar(in)
			* EnvGen.ar(Env.asr,gate,doneAction:2);
			RecordBuf.ar(soundin,bufnum,loop:1);
		}).send(server);

		SynthDef(\sv_playbuf,{
			arg out = 0, bufnum = 0, gate = 0,
			amp = 0, rate = 1, bend = 0, ramp = 0.5;
			var playback = PlayBuf.ar(1, bufnum,
				rate * pow(2, bend / 12.0), loop: 1) *
			EnvGen.ar(Env.asr(ramp),
				gate,amp,doneAction:2);
			Out.ar(out, playback);
		}).send(server);

		// this.out is put for input and output because we will be
		// replacing the contents of this bus (by a scaled copy)
		SynthDef(\sv_masterVolume,{
			arg out = 0, in = 0, gain = 0;
			ReplaceOut.ar(out,In.ar(in) * gain.dbamp);
		}).send(server,
			this.masterVolume.addAfterMsg(this.group,
				[\in,this.out,\out,this.out]));

		// allocate note allocation arrays,
		// assumes no note less than 0 or greater than 127 will come in

		this.recordingSynths = Array.fill(128,{nil});
		this.playingSynths = Array.fill(128,{nil});
		this.busyBufs = Array.fill(polyphony,{-1});

		// ramp time, can be set later
		this.rampTime = 0.1;

		this.noteOnFunc = { arg pitch, velocity;
			var bufToUse, bundle = nil;
			// Free a note of the same number that is already playing
			if ((this.playingSynths[pitch].notNil), {
				bundle = this.server.makeBundle(false,{
					this.playingSynths[pitch].set(\gate,0);
					this.playingSynths[pitch] = nil;
					this.recordingSynths[pitch].set(\gate,0);
					this.recordingSynths[pitch] = nil;
					this.busyBufs[this.busyBufs.indexOf(pitch)] = -1;
				});
			});

			// Check to see if a buffer is free to record into
			bufToUse = this.busyBufs.indexOf(-1);
			if((bufToUse.notNil),{
				this.server.makeBundle(nil,{
					// Store the note number using the buffer
					this.busyBufs[bufToUse] = pitch;
					this.recordingSynths[pitch] = Synth(\sv_recbuf,
						[\gate,1,\bufnum,this.buffers[bufToUse]],this.group);
					this.playingSynths[pitch] = Synth.after(
						this.recordingSynths[pitch],\sv_playbuf,[\gate,1,
							\bufnum,this.buffers[bufToUse],
							\amp,(velocity-127).linlin(-127,0,-40,0).dbamp,
							\rate,pow(2,(pitch - 60)/12.0) - 0.0001,
							\ramp,this.rampTime,\out,this.out]);
				},bundle);
			});
		};

		this.noteOffFunc = { arg pitch, velocity;
			if ((this.playingSynths[pitch].notNil), {
				this.server.makeBundle(nil,{
					this.playingSynths[pitch].set(\gate,0);
					this.playingSynths[pitch] = nil;
					this.recordingSynths[pitch].set(\gate,0);
					this.recordingSynths[pitch] = nil;
					this.busyBufs[this.busyBufs.indexOf(pitch)] = -1;
				});
			});
		};
	}

	noteOn { arg pitch, velocity;
		this.noteOnFunc.(pitch,velocity);
	}

	noteOff { arg pitch, velocity;
		this.noteOffFunc.(pitch,velocity);
	}

	free {
		this.buffers.free;
		this.playingSynths.free;
		this.recordingSynths.free;
		this.busyBufs.free;
		this.group.free;
		super.free;
	}

}


		