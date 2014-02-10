ASeq1 {
 	var <>playFunc, <>eventDict, <>form, <>routine;

 	*new { arg playFunc, eventDict, form;
		^super.new.init(playFunc, eventDict, form);
	}


 	init { arg playFunc, eventDict, form;
		// can be any function that accepts whatever environment the event dict supplies
		this.playFunc = playFunc;
		// supplies the environments for the function, one field must be called "delta" and
		// hold the time to wait for the next event
		this.eventDict = eventDict;
		// an array of dictionary keys that will be played, must not contain any that aren't
		// in the dict
		this.form = form;
		this.routine = Routine({});
	}

	initRoutine {
		 this.routine = Routine({
 			loop {
 				this.form.do({ |key|
 					key.postln;
 					this.eventDict[key].do({ |event|
 						if(event.notNil,{
							this.playFunc.valueWithEnvir(event);
 						});
 						event.delta.yield;
 					});
 				});
 			}
 		});
	}

	schedRoutine { arg tempoClock, delta = 0;
		this.initRoutine;
		tempoClock.sched(delta,this.routine);
	}

	stopRoutine { this.routine.stop; }
}	