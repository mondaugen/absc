MidiCCController {
	// the ccCallBack CallBack should expect a dictionary of values:
	// (val: 69), that is, they expect only one argument
	var <>midiCCFunc, <>channel, <>source,
	<>ccCallBack, <>controlNumber;

	*new { arg ccCallBack, controlNumber, channel, source=nil;
		^super.new.init(ccCallBack, controlNumber, channel, source);
	}

	init { arg ccCallBack, controlNumber, channel, source;
		this.channel = channel;
		this.source = source;
		this.controlNumber = controlNumber;
		this.ccCallBack = ccCallBack;
		this.midiCCFunc = MIDIFunc.cc({
			arg val;
			this.ccCallBack.value((val: val));
		},ccNum: this.controlNumber, chan: this.channel, srcID: this.source);
	}

	free {
		this.midiCCFunc.free;
		super.free;
	}
}