TranspositionControllerTest {
	var <>onRRC, <>offRRC, <>mnc, <>cbOn, <>cbOff, <>onTrans, <>offTrans;

	*new { arg lowerBound=60, upperBound=72, transposition = -12;
		^super.new.init(lowerBound,upperBound,transposition);
	}

	init { arg lowerBound, upperBound, transposition;
		cbOn = {|args| postf("NoteOn: % %\n",args.pitch,args.velocity)};
		onTrans = TranspositionController.new(cbOn,transposition);
		onRRC = RestrictRangeController.new(onTrans.givenCallBack,lowerBound,upperBound);
		cbOff = {|args| postf("NoteOff: % %\n",args.pitch,args.velocity)};
		offTrans = TranspositionController.new(cbOff,transposition);
		offRRC = RestrictRangeController.new(offTrans.givenCallBack,lowerBound,upperBound);
		mnc = MidiNoteController.new(onRRC.givenCallBack,offRRC.givenCallBack,1);
	}

	free {
		mnc.free;
		super.free;
	}
}