MidiNoteControllerTest {
	var <>midiNoteController;

	*new {
		^super.new.init;

	}

	init {
		midiNoteController = MidiNoteController.new({
			arg pitch, velocity;
			"Note On: Pitch ".post; pitch.post; " Vel: ".post; velocity.postln;
			},{
			arg pitch, velocity;
			"Note Off: Pitch ".post; pitch.post; " Vel: ".post; velocity.postln;
		}, 1);
	}

	free {
		midiNoteController.free;
		super.free;
	}
}