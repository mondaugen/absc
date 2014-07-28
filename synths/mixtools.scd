// Synth defs for spreading signals to 2 or more channels

// Spreads a mono signal to stereo
SynthDef(\out_1_2,{
	arg in = 0, out = #[0,1];
	ReplaceOut.ar(out,In.ar(in));
}).store;

// Passes signal unchanged through and allows sending to an auxiliary bus
SynthDef(\aux,{
	arg in = 0, out = 0, aux_gain = 0, aux;
	var insig = In.ar(in);
	Out.ar(aux,insig * aux_gain.dbamp);
	Out.ar(out, insig);
}).store;

// Takes two busses and mixes them down into 1
SynthDef(\mix_2_1,{
	arg in1, in2, out = 0;
	Out.ar(out,In.ar(in1) + In.ar(in2));
}).store;

// Sums a signal into a bus, sort of looks like this:
// bus before: bus after:
// |           | /
// |           |/
// |           |
// mono version
SynthDef(\sumin_1_1,{arg bus = 0, in = 0, gain = 0;
	ReplaceOut.ar(bus,In.ar(bus) + (In.ar(in) * gain.dbamp));
}).store;

// Applies simple gain to a bus
SynthDef(\gain_1_1,{arg bus = 0, gain = 0;
	ReplaceOut.ar(bus,In.ar(bus) * gain.dbamp);
}).store;