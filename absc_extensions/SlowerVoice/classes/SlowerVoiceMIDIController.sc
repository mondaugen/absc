SlowerVoiceMIDIController {
	var <>onRRC, <>offRRC, <>mnc, <>cbOn, <>cbOff, <>onTrans, <>offTrans, <>slowerVoice,
	<>masterVolumeMidiCC, <>bendMidiCC, <>rampMidiCC;

	*new { arg server, in, out, polyphony, lowerBound = -inf, upperBound = 60,
		bufferLength = 30, transposition = 0, masterVolumeCN=22, bendCN=23, rampCN=24,
		channel = 0, source = nil;
		^super.new.init(server, in, out, polyphony, lowerBound,
		upperBound, bufferLength, transposition, channel, source, masterVolumeCN,
			bendCN, rampCN);
	}

	init { arg server, in, out, polyphony, lowerBound, upperBound,
		bufferLength, transposition, channel, source, masterVolumeCN, bendCN, rampCN;
		slowerVoice = SlowerVoice.new(server,polyphony,in,out,bufferLength);
		cbOn = { |args| slowerVoice.noteOnFunc.valueWithEnvir(args); };
		onTrans = TranspositionController.new(cbOn,transposition);
		onRRC = RestrictRangeController.new(onTrans.givenCallBack,lowerBound,upperBound);
		cbOff = { |args| slowerVoice.noteOffFunc.valueWithEnvir(args); };
		offTrans = TranspositionController.new(cbOff,transposition);
		offRRC = RestrictRangeController.new(offTrans.givenCallBack,lowerBound,upperBound);
		mnc = MidiNoteController.new(onRRC.givenCallBack,offRRC.givenCallBack,
			channel,source);
		masterVolumeMidiCC = MidiCCController.new({ |args|
			this.slowerVoice.masterVolume.set(\gain,args.val.linlin(0,127,-60,0));
		},masterVolumeCN,channel,source);
		bendMidiCC = MidiCCController.new({ |args|
			this.slowerVoice.group.set(\bend,args.val.linlin(0,127,-12,12));
		},bendCN,channel,source);
		rampMidiCC = MidiCCController.new({ |args|
			this.slowerVoice.rampTime = args.val.linexp(0,127,0.1,5.0);
		},rampCN,channel,source);
	}

	free {
		mnc.free;
		slowerVoice.free;
		super.free;
	}
}