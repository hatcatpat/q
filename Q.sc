Q {
	var s,sample_dict, fx_dict, fx_groups, kr_dict,kr_bus_count, df_dict, cc, midi_dict, stretch;
	*new {
		arg server, midi=false;
		^super.new.init(server, midi)
	}
	init {
		arg server, midi;
		s = server;

		fx_dict = Dictionary.new;
		fx_groups = 2;

		kr_dict = Dictionary.new;
		kr_bus_count = 0;

		df_dict = Dictionary.new;

		stretch = 8;

		Event.addEventType(\null,{});


		(Platform.userExtensionDir++"/q/Setup/synthdefs.scd").load;
		(Platform.userExtensionDir++"/q/Setup/snippets.scd").load; // requires DDWSnippets Quark
		this.loadSamples;

		if(midi == true){ this.loadMidi; };
	}
	set_stretch{ |x| stretch = x;  }
	get_kr_dict { ^kr_dict }
	get_kr_bus_count { ^kr_bus_count }
	inc_bus_count { kr_bus_count = kr_bus_count + 1 }
	fx {
		arg name;
		if (fx_dict.at(name) == nil){
			var g  = Group.new;
			fx_dict.add( name -> [fx_groups, g] );
			Synth(\out, [\bus,fx_groups], g, 'addToTail');
			fx_groups = fx_groups + 1;
		};
		^fx_dict.at(name);
	}
	bpm { // sets bpm
		arg b;
		TempoClock.default.tempo = (b/60);
	}
	scanBindArgs {
		arg args;
		if (args.includes(\of)){
			var index = args.indexOf(\of);
			args[index] = \timingOffset;
			args[index+1] = args[index+1] / stretch;
		};
		if (args.includes(\gr)){
			var index = args.indexOf(\grp);
			args[index] = \group;
		};
		if (args.includes(\dur)){
			var index = args.indexOf(\dur);
			args[index+1] = args[index+1] / stretch;
		};
		if (args.includes(\fx)){
			var index = args.indexOf(\fx);
			var fx = args[index+1];
			args.removeAt(index);
			args.removeAt(index);
			args = args++[\bus,fx.b,\group,fx.g];
		};
		^args;
	}
	scanArgs {
		arg args;
		var repeats=inf, stutter=0, dstut=0;
		if (args.includes(\r)){
			var index = args.indexOf(\r);
			repeats = args[index+1];
			args.removeAt(index);
			args.removeAt(index);
		};
		if (args.includes(\st)){
			var index = args.indexOf(\st);
			stutter = args[index+1];
			args.removeAt(index);
			args.removeAt(index);
		};
		if (args.includes(\dst)){
			var index = args.indexOf(\dst);
			dstut = args[index+1];
			args.removeAt(index);
			args.removeAt(index);
		};
		^[arr:args,r:repeats,st:stutter,dst:dstut];
	}
	arrayGet{
		arg arr,item;
		var r = nil;
		if ( arr.includes(item) ){
			var index = arr.indexOf(item);
			r = arr[index + 1];
		};
		^r;
	}
	genericSeq {
		arg type, args;
		var p, scan, repeats, stutter, dstut;
		scan = this.scanArgs(args);
		repeats = this.arrayGet(scan, \r);
		stutter = this.arrayGet(scan, \st);
		dstut = this.arrayGet(scan, \dst);
		args = this.arrayGet(scan, \arr);
		args.postln;
		p = switch(type,
			\r, { Prand(args,repeats) },
			\rx, { Pxrand(args,repeats) },
			\rw, { Pwrand(args[0],args[1].normalizeSum,repeats) },
			\s, { Pseq(args,repeats) },
			\sh, { Pshuf(args,repeats) },
			\in, { Pindex(args[0..(args.size-2)],args.last,repeats) },
			\p, { Ppatlace(args,repeats) }
		);
		if (stutter != 0){
			^Pstutter(stutter, p);
		};
		if (dstut != 0){
			^PdurStutter(dstut, p);
		};

		^p;
	}

	loadSamples {
		sample_dict = Dictionary.new;
		sample_dict.add(\foldernames -> PathName("Documents/SuperCollider/Samples").entries);
		for (0, sample_dict[\foldernames].size-1,
			{
				arg i;
				sample_dict.add(sample_dict[\foldernames][i].folderName.asSymbol -> sample_dict[\foldernames][i].entries.collect({
					arg sf;
					Buffer.read(s,sf.fullPath);
				});
		)});
	}
	loadMidi {
		var types = ["s","k"];
		midi_dict = Dictionary.new;

		types.do({|t|
			8.do({|num|
				var name = (t++(num%8) ).asSymbol;
				midi_dict[name] = 1;
				Pdefn(name, 1);
			});
		});

		midi_dict[\buttons] = Array.fill2D(3,8,{ {} });

		MIDIClient.init;
		MIDIIn.connectAll;

		cc = MIDIFunc.cc({
			arg val, num, chan, source;
			var type="b";

			if( (0 <= num) && (num < 8) ){ type = types[0]; }{
				if(num < 24){ type = types[1]; };
			};

			if(type!="b"){
				var name = (type++(num%8) ).asSymbol;
				PRINT(name,val/127);
				midi_dict[name] = val / 127;
				Pdefn(name, val / 127);
			}{
				if(val > 0){
					var row, item;
					if( (32<=num) && (num < 40) ){ row = 0; item = num-32;}{
						if( (48<=num) && (num < 56) ){ row = 1; item = num-48 }{
							if( (64<=num) && (num<72) ){ row = 2; item = num-64; };
					}};
					PRINT(num,row,item);
					if( (row!=nil)&&(item!=nil) ){
						midi_dict[\buttons][row][item].value(item,row);
					};
				};
			};

		});

	}
	freeMidi { cc.free; }
	getSampleDict { ^sample_dict; }
	getDfDict { ^df_dict; }
	getMidiDict { ^midi_dict; }
}