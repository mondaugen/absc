A collection of stuff for SuperCollider.

SETUP
-----

Put this in your startup.scd file:
// load-up absc
{
  // Assuming your absc folder is in ~/Documents/development; change it to fit
  // your setup
	var absc_base = "~/Documents/development/absc".standardizePath;
	var absc_startup_path = PathName(absc_base) +/+ PathName("normal_startup.scd");
	var file = File(absc_startup_path.fullPath,"r");
	var str = file.readAllString;
	str.interpret;
	file.close;
	~absc_normal_startup.value(absc_base);
}.value;

go to your Extensions directory and do
ln -s /path/to/absc_extensions

and you should be good to go.

Copyright 2013 Nicholas Esterer. All rights reserved.
