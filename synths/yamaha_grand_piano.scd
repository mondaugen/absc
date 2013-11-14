// Routines for loading in piano samples and playing them

~ygp_init = {
	~ygp_dict = IdentityDictionary.new();
	~ygp_pkeys = Array.new(); // Array of possible pitches that can be played.
	~ygp_buff_array = Array.new(); // Array of the buffers we have (so they can quickly be freed)
	PathName.new("~/Documents/sound/logic-samples/yamaha_grand_piano/").entries.do({
		|entry|
		if ( entry.notNil, {
			var fullPath = entry.fullPath;
			var pitch = fullPath.findRegexp("[0-9]+")[0][1].asInt;
			var dyn   = fullPath.copyRange(0,fullPath.findAll(".").last
				- 1).findRegexp("\(H\|M\|S\|h\|mf\|s\)").last[1];
			var sus = (dyn.compare("h") == 0) || (dyn.compare("mf") == 0) || (dyn.compare("s") == 0);
			var dynNum;
			var curBuf;
			if ( ~ygp_dict.includesKey(pitch).not, { ~ygp_dict.put(pitch,IdentityDictionary.new()) });
			if ( ~ygp_dict[pitch].includesKey(sus).not, { ~ygp_dict[pitch].put(sus,IdentityDictionary.new()) });
			switch ( dyn,
				"H", {dynNum = 3},
				"M", {dynNum = 2},
				"S", {dynNum = 1},
				"h", {dynNum = 3},
				"mf", {dynNum = 2},
				"s", {dynNum = 1});
			curBuf = Buffer.read(s,fullPath);
			~ygp_dict[pitch][sus].put(dynNum, curBuf);
			~ygp_buff_array = ~ygp_buff_array.add(curBuf);
			if ( ~ygp_pkeys.indexOf(pitch) == nil, {~ygp_pkeys = ~ygp_pkeys.add(pitch)});
			~ygp_pkeys.sort;

		});
	});
};

~ygp_free = { ~ygp_buff_array.do({ arg buf; buf.free }) };

// play piano in real time
~ppno_rt = {
	arg pitch = 60, sus = false, dyn = 0.5, dbmin = -30, dbmax = 0, out = 0, length = 1,
	server = s, bundleDelay = 0.25, detuneCents = 0, decay = 0.01, cutoffmin = 400,
	cutoffmax = 5000;
	var dynIdx = (dyn * 3).ceil.asInt;
	var	dynAmp = dyn.linlin(0, 1, dbmin, dbmax);
	var dynCutoff = dyn.linlin(0,1,cutoffmin,cutoffmax);
	var	pIdx = ~ygp_pkeys.indexIn(pitch);
	var cpch = ~ygp_pkeys[pIdx]; // The pitch that's in the dictionary.
	var diff = pitch - cpch;  	 // The difference therefrom
	// The rate as a result of the difference
	var rate = 2 ** ((diff.asFloat + (detuneCents/100)) / 12.0);
	var buf;
	if ( ~ygp_dict[cpch].includesKey(sus).not, { sus = sus.not });
	if (~ygp_dict[cpch][sus].includesKey(dynIdx),
		{ buf = ~ygp_dict[cpch][sus][dynIdx] },
		{ buf = ~ygp_dict[cpch][sus][~ygp_dict[cpch][sus].keys.asArray.sort.first] });
	if ( buf.notNil,
		{
			var bufLen = ((buf.numFrames / server.sampleRate) / buf.numChannels) / rate;
			if ((bufLen < length),{length = bufLen});
			server.makeBundle(bundleDelay,{ Synth(\pblelpf,
				[\bufnum, buf,
					\rate, rate,
					\gain, dynAmp,
					\out, out,
					\length, length,
					\decay,decay,
					\cutoff,dynCutoff])}) },
		{ "couldn't play".post; dynIdx.post; buf.postln });
};
