MonoMixer {
	var <>mixGroup, <>mixPairs, <>mixBus, <>outGain;

	*new {
		^super.new.init;
	}

	init {
		this.mixGroup = Group.new;
		this.mixPairs = List[];
		this.mixBus = Bus.audio;
		this.outGain = Synth.after(this.mixGroup,\gain_1_1,[\bus,this.mixBus]);
	}

	free {
		this.mixPairs.do({|synth| synth.free});
		this.mixBus.free;
		this.mixGroup.free;
		super.free;
	}

	addInput { arg inBus;
		this.mixPairs.add(Synth(\sumin_1_1,[\bus,this.mixBus,\in,inBus],this.mixGroup));
	}

	// removes input at index and frees the sumin_1_1 node that is returned
	removeInputAt { arg index = -1;
		while({index < 0}, {index = index + this.mixPairs.size});
		this.mixPairs.removeAt(index).free;
	}
}
	