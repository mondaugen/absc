MidiNoteController {
	// the noteOn/Off CallBacks should expect a dictionary of values:
	// (pitch: 50,velocity: 69), that is, they expect only one argument
	var <>midiOnFunc, <>midiOffFunc, <>channel, <>source,
	<>noteOnCallBack, <>noteOffCallBack;

	*new { arg noteOnCallBack, noteOffCallBack, channel, source=nil;
		^super.new.init(noteOnCallBack, noteOffCallBack, channel, source);
	}

	init { arg noteOnCallBack, noteOffCallBack, channel, source;
		this.channel = channel;
		this.source = source;
		this.noteOnCallBack = noteOnCallBack;
		this.noteOffCallBack = noteOffCallBack;
		this.midiOnFunc = MIDIFunc.noteOn({
			arg val, num, chan, src;
			this.noteOnCallBack.value((pitch: num, velocity: val));
		},chan: this.channel, srcID: this.source);
		this.midiOffFunc = MIDIFunc.noteOff({
			arg val, num, chan, src;
			this.noteOffCallBack.value((pitch: num, velocity: val));
		},chan: this.channel, srcID: this.source);
	}

	free {
		this.midiOnFunc.free;
		this.midiOffFunc.free;
		super.free;
	}
}