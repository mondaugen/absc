SequenceTriggerMIDI {
	var <>key, <>channel, <>renderFunc, <>aSeq, <>midiFunc, <>tempoClock, <>quant, <>phase,
	<>dictChangeFunc;

	*new { arg key, channel, renderFunc, aSeq, tempoClock,
		quant = 1, phase = 0;
		^super.new.init(key, channel, renderFunc, aSeq, tempoClock, quant, phase);
	}

 	init { arg key, channel, renderFunc, aSeq, tempoClock, quant, phase;
		// note to listen for to trigger the rendering
		this.key = key;
		// channel to listen on
		this.channel = channel;
		// the function that accepts the note number, velocity and
		// renders the dictionary that aSeq will play
		this.renderFunc = renderFunc;
		this.aSeq = aSeq;
		this.tempoClock = tempoClock;
		this.quant = quant;
		this.phase = phase;
		this.dictChangeFunc = { |num,val|
			this.aSeq.eventDict = this.renderFunc.value(num,val);
			this.aSeq.schedRoutineAbs(this.tempoClock,
				this.tempoClock.nextTimeOnGrid(this.quant,this.phase));
		};
		this.initMidiFunc;
	}

	initMidiFunc {
		this.midiFunc = MIDIFunc.new({
			arg val, num, chan, src;
			if(this.aSeq.routine.isPlaying,
				{ this.aSeq.stopRoutine; },
				{ this.dictChangeFunc.value(num,val); }
			);
		},this.key,this.channel,\noteOn);
	}

	freeMidiFunc {
		this.midiFunc.free;
		this.midiFunc = nil;
	}

	forceDictChange { |num,val|
		this.aSeq.stopRoutine;
		this.dictChangeFunc.value(num,val);
	}

}