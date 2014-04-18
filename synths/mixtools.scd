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