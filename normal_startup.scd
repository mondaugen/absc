// This is the path to the root of the absc repository
~absc_full_path;

// This is a startup file that is read every time SC starts up
// and defines functions and stuff that are commonly used.
~absc_normal_startup = {
	arg aPath; // The absolute path to the base of absc/, so something like "~/my/stuff/absc"
	// List of folders from absc whose contents we want to be executed
	var absPath = PathName(aPath.standardizePath);
	~absc_full_path = aPath.standardizePath;
	["synthcontrol"].do({
		arg rPath;
		var newPath = absPath +/+ PathName(rPath);
		// newPath.postln;
		// newPath.files.postln;
		newPath.filesDo({
			arg fPath;
			var file = File(fPath.fullPath, "r");
			// fPath.fullPath.postln;
			file.readAllString.interpret;
			file.close;
		});
	});
};

// This will (re-)store all the synth defs in the absc repository using the ~absc_full_path variable.
~absc_store_synths = {
	var absPath = PathName(~absc_full_path);
	["synths"].do({
		arg rPath;
		var newPath = absPath +/+ PathName(rPath);
		// newPath.postln;
		// newPath.files.postln;
		newPath.filesDo({
			arg fPath;
			var file = File(fPath.fullPath, "r");
			// fPath.fullPath.postln;
			file.readAllString.interpret;
			file.close;
		});
	});
};