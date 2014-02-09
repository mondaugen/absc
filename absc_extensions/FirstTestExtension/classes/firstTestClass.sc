FirstTestClass {
	var <>a;

	*new { arg a;
		^super.newCopyArgs(a);
	}
}