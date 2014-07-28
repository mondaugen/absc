TaskListSequencer {
	// Plays through an array of lists of functions at a speed dictated by "length/divisions"
	// Each function is passed the arguments tidx,lidx,list,func which are the time (the index of the array),
	// the list index, the list itself, and the function itself.
	// This could be useful for functions that remove themselves
	// after being played.

	var <>task, <>sequence, <>length, <>curIdx;

	*new { arg divisions = 8, length = 1;
		^super.new.init(divisions, length);
	}

	init { arg divisions, length;
		this.sequence = Array.new;
		this.length = length;
		divisions.do({ this.sequence = this.sequence.add(List.new(0)); });
		this.curIdx = 0;
		this.task = Task({
			loop {
				this.sequence.do({ |list,tidx|
					this.curIdx = tidx;
					list.do({ |func,lidx| func.value(tidx,lidx,list,func); });
					(this.length/divisions).wait;
				});
			}
		});
	}

}