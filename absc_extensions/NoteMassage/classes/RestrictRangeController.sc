RestrictRangeController {
	// givenCallBack is the callback this object provides after it's been made
	// takenCallBack is the callback this object takes and calls when the note
	// in the argument list is between the good range
	var <>givenCallBack, <>takenCallBack, <>lowerBound, <>upperBound;

	*new {arg takenCallBack, lowerBound, upperBound;
		^super.new.init(takenCallBack,lowerBound,upperBound);
	}

	init { arg takenCallBack, lowerBound, upperBound;
		this.takenCallBack = takenCallBack;
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
		this.givenCallBack = {
			arg args;
			if((args.pitch >= this.lowerBound).and(
				args.pitch <= this.upperBound),{
				this.takenCallBack.value(args);
			});
		};
	}
}
		