RestrictRangeControllerTest {
	var <>onRRC, <>offRRC, <>mnc, <>cbOn, <>cbOff;

	*new { arg lowerBound=60, upperBound=72;
		^super.new.init(lowerBound,upperBound);
	}

	init { arg lowerBound, upperBound;
		cbOn = {|args| postf("NoteOn: % %\n",args.pitch,args.velocity)};
		cbOff = {|args| postf("NoteOff: % %\n",args.pitch,args.velocity)};
		onRRC = RestrictRangeController.new(cbOn,lowerBound,upperBound);
		offRRC = RestrictRangeController.new(cbOff,lowerBound,upperBound);
		mnc = MidiNoteController.new(onRRC.givenCallBack,offRRC.givenCallBack,1);
	}

	free {
		mnc.free;
		super.free;
	}
}

