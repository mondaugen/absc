SynthDef(\saw_efm,{
	|out=#[0,1], freq = 440, minfreq = 100, maxfreq = 1000, attack = 0.01, decay = 3,
	fattack = 1, fdecay = 1, amp = -3, detune = 1, dmodrate = 0.1, dmoddepth = 0.01,
	dmodphase = 0, rq = 0.5, amdepth = 0, amrate = 0, dmodenvdepth = 0, dmodenvattack = 0.05,
	dmodenvdecay = 0.5|
	// phase is between 0 and 1 and is multiplied by 2pi
	Out.ar(out,
		(1 - amdepth + SinOsc.kr(amrate,0,amdepth))
		* BLowPass.ar((Saw.ar(freq)
			+ Saw.ar(freq * (detune + SinOsc.kr(dmodrate,dmodphase*2pi,
				dmoddepth + (EnvGen.kr(
					Env.perc(dmodenvattack,dmodenvdecay,dmodenvdepth*amp.dbamp)))))))/2
			* EnvGen.kr(Env.perc(attack,decay,amp.dbamp),doneAction: 2),
			LinLin.kr(EnvGen.kr(Env.perc(fattack,fdecay,amp.dbamp)),0,1,minfreq,maxfreq),
			rq));
}).store;

SynthDef(\saw_efm_hpn,{
	|out = #[0,1], freq = 440, minfreq = 400, maxfreq = 1000, attack = 0.5, decay = 3,
	fattack = 1, fdecay = 1, amp = -3, detune = 1, dmodrate = 0.1, dmoddepth = 0.01,
	dmodphase = 0, rq = 0.5, hpcutoff = 400, hprq = 1, notchfreq = 600, notchrq = 1,
	notchdb = -6, onotchfreq = 1000, onotchrq = 1, onotchdb = -6, amdepth = 0.1, amrate = 2.0,
	dmodenvdepth = 0, dmodenvattack = 0.05, dmodenvdecay = 0.5|
	// phase is between 0 and 1 and is multiplied by 2pi
	Out.ar(out,
		(1 - amdepth + SinOsc.kr(amrate,0,amdepth))
		* MidEQ.ar(
			MidEQ.ar(
				BHiPass.ar(BLowPass.ar((Saw.ar(freq)
					+ Saw.ar(freq * (detune + SinOsc.kr(dmodrate,dmodphase*2pi,dmoddepth
						+ (EnvGen.kr(
							Env.perc(dmodenvattack,dmodenvdecay,dmodenvdepth*amp.dbamp)))))))/2
					* EnvGen.kr(Env.perc(attack,decay,amp.dbamp),doneAction: 2),
					LinLin.kr(EnvGen.kr(Env.perc(fattack,fdecay,amp.dbamp)),0,1,minfreq,maxfreq),
				rq),hpcutoff, hprq),
				notchfreq,
				notchrq,
				notchdb),
			onotchfreq,
			onotchrq,
			onotchdb));
}).store;