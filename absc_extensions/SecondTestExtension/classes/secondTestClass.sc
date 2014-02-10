SecondTestClass {
	var <>a;

	*new { arg a;
		^super.new.init(a);
	}

	init {  arg a;
		this.a = a + 1;
	}

	add {
		arg val;
		^(a + val);
	}
}