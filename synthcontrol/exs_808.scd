// Routines for loading in drum samples and playing them

~drum808_init = {
	~drum808_buff_array = Array.new(); // Array of the buffers we have (so they can quickly be freed)
	PathName.new("~/Documents/sound/logic-samples/exs_808_less/").entries.do({
		|entry|
		if ( entry.notNil, {
			var fullPath = entry.fullPath;
			var curBuf;
			curBuf = Buffer.read(s,fullPath);
			~drum808_buff_array = ~drum808_buff_array.add(curBuf);
		});
	});
};

~drum808_free = { ~drum808_buff_array.do({ arg buf; buf.free }) };

// play drum in real time
~p808_rt = {
	arg pitch = 1, gain = 0.5, out = 0, length = 1,
	server = s, bundleDelay = 0.25, rate = 1, decay = 0.01, cutoff = 1000,
	addAction = \addToHead;
	server.makeBundle(bundleDelay,{
		if((pitch < ~drum808_buff_array.size),{
			Synth(\pblelpf,
				[\bufnum, ~drum808_buff_array[pitch],
					\rate, rate,
					\gain, gain,
					\out, out,
					\length, length,
					\decay,decay,
					\cutoff,cutoff], addAction: addAction) },
			{ "couldn't play (808)".post; pitch.postln;});
	});
};
