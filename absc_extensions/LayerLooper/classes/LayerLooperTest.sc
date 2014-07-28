LayerLooperTest {
	var <>inBus, <>outBus, <>recordGroup, <>playGroup, <>server, <>tempoClock, <>layerLooper,
	<>inputNode, <>outputNode, <>stereoSpreader;

	*new { |server|
		^super.new.init(server);
	}

	init { |server|
		this.server = server;
		this.inBus = Bus.audio(this.server);
		this.outBus = 0;
		this.recordGroup = Group.new(this.server);
		this.playGroup = Group.new(this.recordGroup,'addAfter');
		this.inputNode = Synth.basicNew(\l_input,this.server);
		this.outputNode = Synth.basicNew(\l_output,this.server);
		this.tempoClock = TempoClock.default;
		this.layerLooper = LayerLooper.new(this.server,this.inBus,this.outBus,this.recordGroup,
			this.playGroup,this.tempoClock);

		// Synth for mixing input and feedback
		SynthDef(\l_input,{|in,out,fb_in,fb_gain = 0|
			var sig = SoundIn.ar(in);// + (InFeedback.ar(fb_in) * fb_gain.dbamp);
			Out.ar(out,sig);
		}).send(this.server,this.inputNode.addBeforeMsg(this.recordGroup,[\in,0,
			\out,this.inBus]));

		// Synth for mixing output and feedback
		SynthDef(\l_output,{|in,out,fb_out,fb_gain = -inf|
			var sig = In.ar(in);
			//Out.ar(fb_out,sig * (fb_gain.dbamp));
			Out.ar(out,sig);
		}).send(this.server,this.outputNode.addAfterMsg(this.playGroup,
			[\in,this.outBus,\out,0]));

		// this.stereoSpreader = Synth.after(this.outputNode,\out_1_2);
	}
}