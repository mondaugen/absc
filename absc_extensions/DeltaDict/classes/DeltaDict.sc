DeltaDict : Dictionary {

	// Takes dictionary of (key: value...) pairs where the keys are numbers and returns an
	// array like this: [[difference_key1_key2,value],[difference_key2_key_3,value],...]

	toDeltaValuePairs { |start=0|
		var result = Array.new;
		this.keys.asArray.sort.do({|key|
			result = result.add([key - start,this[key]]);
			start = key;
		});
		^result;
	}

	// Returns a routine that will use the first item of each pair and use it as a yield
	// value for a routine, and use the second value as the input to a function you supply
	toRoutine { |func|
		^Routine({
			this.toDeltaValuePairs.do({ |val|
				func.value(val[1]);
				val[0].yield;
			});
		});
	}
}