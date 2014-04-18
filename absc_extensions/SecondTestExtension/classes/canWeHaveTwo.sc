CanWeHaveTwo {
	// YesWeCan
	var <>a;

	*new { arg a;
		^super.new.init(a);
	}

	init {  arg a;
		this.a = a + 5;
	}

	addMore {
		arg val;
		^(a + val);
	}
}