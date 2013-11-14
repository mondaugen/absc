// Synths for playing buffers

SynthDef(\pble, {
	arg out = #[0,1], bufnum, rate = 1.0, gain = -3, length = 1, attack = 0, decay = 0.1;
	Out.ar(out,
		EnvGen.kr(
			Env.new(
				[0,1,1,0],
				[attack, length - attack - decay, decay]),
			levelScale: gain.dbamp,
			doneAction: 2) * PlayBuf.ar(1, bufnum, BufRateScale.kr(bufnum) * rate))
}).send(s);

SynthDef(\pblelpf, {
	arg out = #[0,1], bufnum, rate = 1.0, gain = -3, length = 1, attack = 0, decay = 0.1,
	cutoff = 1000;
	Out.ar(out,
		LPF.ar(EnvGen.kr(
			Env.new(
				[0,1,1,0],
				[attack, length - attack - decay, decay],
				'cubed'),
			levelScale: gain.dbamp,
			doneAction: 2) * PlayBuf.ar(1, bufnum, BufRateScale.kr(bufnum) * rate),cutoff))
}).send(s);
