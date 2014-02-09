SecondTestClass {
	var <>a;

	*new { arg a;
		^super.newCopyArgs(a);
	}

	add {
		arg val;
		^(a + val);
	}
}