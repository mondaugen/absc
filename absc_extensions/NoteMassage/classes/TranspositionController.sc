TranspositionController {
	// transposes incoming notes by a set amount
	var <>takenCallBack, <>givenCallBack, <>transposition;

	*new { arg takenCallBack, transposition=0;
		^super.new.init(takenCallBack, transposition);
	}

	init { arg takenCallBack, transposition;
		this.takenCallBack = takenCallBack;
		this.transposition = transposition;
		this.givenCallBack = {
			arg args;
			args.pitch = args.pitch + this.transposition;
			this.takenCallBack.value(args);
		}
	}
}

		