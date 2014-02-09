// Load files in a directory into buffers.
// The buffers are stored in a dictionary where the keys are midi note numbers.
// Each value associated with a key is an array of buffers.
// When a valid soundfile is loaded, its name is scanned for the first number.
// This number represents the key at which it will be stored.
// If there is already a soundfile at this key, it is appended to the array of soundfiles.
// Arguments are a path to a directory to traverse for files,
// a regular expression to parse up to the midinote after the directory path,
// (i.e. ~/this/is/my/path/, this_is_the_regex[0-9]+
// (assuming the directory contains files that start with "this_is_the_regex")
// a dictionary (to look up the buffers), an array (to store the buffers for easy removal)
~bmn_init = {
	arg abspath, re, sfdict, sfary;
	PathName.new(abspath).entries.do({
		|entry|
		if ( entry.notNil, {
			var fullPath = entry.fullPath;
			var fileName = entry.fileName;
			var pitch = fileName.replace(fileName.findRegexp(re)[0][1],"").asInteger;
			var curBuf;
			if ( sfdict.includesKey(pitch).not, {
				sfdict.put(pitch,Array.new())
			});
			curBuf = Buffer.read(s,fullPath);
			sfdict[pitch] = sfdict[pitch].add(curBuf);
			sfary = sfary.add(curBuf);
		});
	});
};

// Pass in your array of buffers to have them all freed
~bmn_free = {
	arg bufary;
	bufary.do({ arg buf; buf.free })
};

// play a buffer stored in a dictionary at a pitch and index in real time
~pbmn_rt = {
	// dictionary holding buffers
	arg sfdict,
	// the available pitches in the dictionary, as an array and sorted
	sfdkeys,
	pitch = 60,
	// the index of that dictionary's entry that you want to play
	index = 0,
	gain = 0, out = #[0,1],
	length = 1,
	server = s, bundleDelay = 0.25, detuneCents = 0, attack = 0.01,
	decay = 0.01, cutoff = 5000, addAction = \addToHead;
	server.makeBundle(bundleDelay,{
		var	pIdx = sfdkeys.indexIn(pitch);
		var cpch = sfdkeys[pIdx]; // The pitch that's in the dictionary.
		var diff = pitch - cpch;  	 // The difference therefrom
		// The rate as a result of the difference
		var rate = 2 ** ((diff.asFloat + (detuneCents/100)) / 12.0);
		var buf = sfdict[cpch][index];
		if ( buf.notNil,
			{
				var bufLen = ((buf.numFrames / server.sampleRate) / buf.numChannels) / rate;
				if ((bufLen < length),{length = bufLen});
				Synth(\pblelpf,
					[\bufnum, buf,
						\rate, rate,
						\gain, gain,
						\out, out,
						\attack, attack,
						\length, length,
						\decay,decay,
						\cutoff,cutoff],addAction: addAction) },
			{ "couldn't play pitch, index: ".post; pitch.post; index.postln });
	});
};
