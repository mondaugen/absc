TaskSequencer {

	var <>task, <>sequence, <>length;

	*new { arg divisions = 8, length = 1;
		^super.new.init(divisions, length);
	}

	init { arg divisions, length;
		this.sequence = Array.new;
		this.length = length;
		divisions.do({ this.sequence = this.sequence.add(nil); });
		this.task = Task({
			loop {
				this.sequence.do({ |func,idx|
					func.value(idx);
					(this.length/divisions).wait;
				});
			}
		});
	}

}