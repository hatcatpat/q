DF { // pdef
	*new{
		arg key,item=nil,when=1,update_dict=true;
		if (item != nil){
			var ret = Pdef(key, item).quant_(when).play;
			if(update_dict == true){
				~q.getDfDict[key] = item;
			};
		} {
			Pdef(key).clear;
			~q.getDfDict.removeAt(key);
		};
	}
}
DFN { // pdefn
	*new{
		arg key, item;
		^Pdefn(key, item );
	}
}
W { // pwhite
	*new{
		arg lo,hi,rnd=nil;
		if(rnd != nil){
			^Pwhite(lo+0.0,hi).round(rnd);
		} {
			^Pwhite(lo+0.0,hi);
		}
	}
}
B {
	*new{
		arg ... items;
		var clutch;
		var ret = nil;
		items.insert(0, \instrument);
		items = ~q.scanBindArgs(items);

		if( items.includes(\cl) ){
			var index = items.indexOf(\cl);
			clutch = items[index+1];
			2.do({ items.removeAt(index) });
		};

		if( items.includes(\fn) ){
			var index = items.indexOf(\fn);
			var func = Pfunc(items[index+1]);
			2.do({ items.removeAt(index) });
			items = items ++ [\func, func];
		};


		ret = Pbind(*items);

		if(clutch != nil){ ret = Pclutch(ret, clutch) };

		^ret
	}
}
M { // pmono
	*new{
		arg ... items;
		var clutch;
		var ret = nil;
		items = ~q.scanBindArgs(items);

		if( items.includes(\fn) ){ // takes a function, i.e {|e| e[\n].postln; }, and turns it into a Pfunc which is placed at the end of the arg sequence
			var index = items.indexOf(\fn);
			var func = Pfunc(items[index+1]);
			2.do({ items.removeAt(index) });
			items = items ++ [\func, func];
		};

		ret = Pmono(*items);

		if(clutch != nil){ ret = Pclutch(ret, clutch) };

		^ret
	}
}

R { // prand
	*new{
		arg ... args;
		^~q.genericSeq(\r, args);
	}
}
RX {
	*new{
		arg ... args;
		^~q.genericSeq(\rx, args);
	}
}
RW {
	*new{
		arg ... args;
		^~q.genericSeq(\rw, args);
	}
}
S { // pseq
	*new{
		arg ... args;
		^~q.genericSeq(\s, args);
	}
}
P { // ppatlace
	*new{
		arg ... args;
		^~q.genericSeq(\p, args);
	}
}
SH { // pshuf
	*new{
		arg ... args;
		^~q.genericSeq(\sh, args);
	}
}
SSH { // pshuf inside pseq
	*new{
		arg ... args;
		^( S(~q.genericSeq(\sh, args) ) );
	}
}
SW { // pswitch1
	*new{
		arg ... args;
		var which = args[0];
		args.removeAt(0);
		^Pswitch1(args, which);
	}
}
ST { // pstutter
	*new{
		arg ... args;
		var n = args[0];
		args.removeAt(0);
		^Pstutter(n, args);
	}
}
IN { // pindex
	*new{
		arg ... args;
		^~q.genericSeq(\in, args);
	}
}
CL { // pclutch;
	*new{
		arg pattern, clutch;
		^Pclutch(pattern,clutch);
	}
}
K { //pkey
	*new{
		arg name;
		^Pkey(name);
	}
}
KR { //pkr
	*new{
		arg name,out_min,out_max,in_min=(-1),in_max=1;
		if (~q.get_kr_dict.at(name) == nil){
			^Pkr(0);
		} {
			^MAP( Pkr(~q.get_kr_dict.at(name)[0]), in_min, in_max, out_min, out_max);
		}
	}
}
KRV { // pkr which returns the value of the bus, not a pfunc
	*new {
		arg name,out_min,out_max,in_min=(-1),in_max=1;
		if (~q.get_kr_dict.at(name) != nil){
			if( (out_min==nil)&&(out_max==nil) ){
				^( ~q.get_kr_dict[name][2].next );
			} {
				^MAP( ~q.get_kr_dict[name][2].next, in_min,in_max,out_min,out_max );
			}
		}
	}
}
BUS { //creates a kr synth node with a given bus
	*new{
		arg ... args;
		var name, synth;
		name = args[0];
		args.removeAt(0);

		if ( args.size > 0 ){
			synth = args[0];
			args.removeAt(0);

			if( args.includes(\t) ){ // use t to make a frequency in terms of the global BPM
				var index = args.indexOf(\t);
				var t = 1 / T(args[index+1]);
				2.do({ args.removeAt(0); });
				args = args ++ [\freq,t];
			};

			if (~q.get_kr_dict.at(name) != nil){
				~q.get_kr_dict.at(name)[1].free;
				~q.get_kr_dict.at(name)[1] = Synth(synth, [bus: ~q.get_kr_dict.at(name)[0]]++args);
			} {
				~q.get_kr_dict.add(name -> [~q.get_kr_bus_count,  Synth(synth, [bus: ~q.get_kr_bus_count]++args), Pkr(~q.get_kr_bus_count).asStream ]);
				~q.inc_bus_count;
			}
		} {
			if (~q.get_kr_dict.at(name) != nil){
				~q.get_kr_dict.at(name)[1].free;
				~q.get_kr_dict.removeAt(name);
			}
		}

	}
}
DE { // degree
	*new{
		arg degree,octave=3;
		if ( (degree.class == Integer)&&(octave.class == Integer) ){
			^( Scale.major.degrees.wrapAt(degree) + (12*octave) );
		}{
			^( Pindex(Scale.major.degrees,degree,inf) + (12*octave) );
		}
	}
}
NOTE { // enter strings NOTE OCT, returns midinote
	*new{
		arg str;
		var note,oct,midinote=60;
		note = str[0].asSymbol;
		oct = str[1..].asInteger;
		note = ~q.lettersToDegree[note];
		midinote = note + (12*(oct+1) );
		^midinote;
	}
}
SC { // scale
	*new{
		arg scale=\major,degree=0,octave=3;
		if ( Scale.at(scale) != nil ){
			^( Pindex( Scale.at(scale).degrees,degree,inf) + (12*octave) );
		} {
			^Pseq([60],inf);
		}
	}
}
D { // (sample) dictionary
	*new{
		arg folder, sample=0, folder_num=0;
		if (folder.size > 1){
			var sample_folders = [];
			folder.do({
				arg f;
				sample_folders = sample_folders++Pindex(~q.getSampleDict.at(f),sample,inf);
			});
			^Pswitch1(sample_folders, folder_num);
		} {
			^Pindex( ~q.getSampleDict.at(folder), sample, inf);
		}
	}
}
DV { // (sample) dictionary value (not a Pindex)
	*new{
		arg folder, sample=0;
		^(~q.getSampleDict.at(folder).wrapAt(sample) );
	}
}
FX { // creates a new fx group + bus
	var name, group, bus;
	*new {
		arg n;
		^super.new.init(n);
	}
	init {
		arg n;
		n = ~q.fx(n);
		group = n[1];
		bus = n[0];
	}
	g { ^group }
	b { ^bus }
}
EU { // euclidean rythmn
	*new {
		arg k,n;
		^Pbjorklund2(k,n);
	}
}
BPM { // sets bpm
	*new{
		arg bpm;
		~q.bpm(bpm);
	}
}
SET { // temporarily overides the parameters of the given pattern names with a list of arguments
	*new {
		arg ... args;
		var names = args[0];
		args.removeAt(0);

		args = ~q.scanBindArgs(args);

		if( names.size == 0 ){
			var name = names;
			var p = ~q.getDfDict[name];
			(args.size/2).do({|i| var j = i*2;
				p = Pset( args[j], args[j+1], p );
			});
			DF(name, p, update_dict:false);
		} {
			names.do({|name|
				var p = ~q.getDfDict[name];
				(args.size/2).do({|i| var j = i*2;
					p = Pset( args[j], args[j+1], p );
				});
				DF(name, p, update_dict:false);
			});
		};

	}
}
ALL { // uses SET on all of the current DFs
	*new{
		arg ... args;
		var names = ~q.getDfDict.keys;
		SET( *([names]++args) );
	}
}
RE { // repeats a single value a number of times
	*new {
		arg value, repeats=1;
		^( S(value,\r,repeats) );
	}
}
FUNC { // a pbind whose arguments are just used as arguments for a function, doesn't play a sound. I.e, calls function with arguments every \dur.
	*new{
		arg ... items;
		var clutch;
		var ret = nil;
		items = items.insert(0, \type);
		items = items.insert(1, \null);
		items = ~q.scanBindArgs(items);

		if( items.includes(\cl) ){
			var index = items.indexOf(\cl);
			clutch = items[index+1];
			2.do({ items.removeAt(index) });
		};

		if( items.includes(\fn) ){
			var index = items.indexOf(\fn);
			var func = Pfunc(items[index+1]);
			2.do({ items.removeAt(index) });
			items = items ++ [\func, func];
		};

		items.postln;
		ret = Pbind(*items);

		if(clutch != nil){ ret = Pclutch(ret, clutch) };

		^ret
	}
}
STOP { // stops given pdefs
	*new {
		arg ... args;
		args.do({|a| DF(a); });
	}
}
MUTE { // mutes given pdefs
	*new {
		arg ... args;
		args.do({|a|
			Pdef(a).clear;
		});
	}
}
UNMUTE { // mutes given pdefs
	*new {
		arg ... args;
		args.do({|a|
			DF(a, ~q.getDfDict[a] );
		});
	}
}
SOLO { // solos given pdefs
	*new {
		arg ... args;

		Pdef.all.keys.do({|p|
			if (args.includes(p)){
				UNMUTE(p);
			} {
				MUTE(p);
			};
		});
	}
}
UNMUTEALL {
	*new {
		~q.getDfDict.keys.do({|k|
			UNMUTE(k);
		});
	}
}
SCHED {
	*new {
		arg func, when=0;
		var full_func = { func.value; nil };
		TempoClock.default.sched(when, full_func);
	}
}
RNG { // maps a region 0 <-> 1 to a region lo <-> hi
	*new {
		arg x, lo=0, hi=1;
		^( x*(hi-lo) + lo );
	}
}
T {
	*new {
		arg x;
		^( x * TempoClock.default.beatDur );
	}
}
MIDI { // returns DFN for a MIDI knob/slider. Optional min/max return values.
	*new{
		arg x,lo,hi;
		x = x.asSymbol;
		if ( (lo!=nil)&&(hi!=nil) ){
			^( DFN(x)*(hi-lo) + lo );
		}{
			^( DFN(x) );
		};
	}
}
MIDIV { // as above but returns a value not a DFN
	*new {
		arg x,lo,hi;
		x = x.asSymbol;
		if ( (lo!=nil)&&(hi!=nil) ){
			^( ~q.getMidiDict[x]*(hi-lo) + lo );
		}{
			^( ~q.getMidiDict[x] );
		};
	}
}
MAP { // maps the given input,min,max to the range out_min <-> out_max
	*new {
		arg x, in_min=(-1), in_max=1, out_min=0, out_max=1;
		^( ( (x - in_min) * (out_max - out_min) / (in_max - in_min) ) + out_min );
	}
}

PRINT { // prints arguments
	*new{
		arg ... args;
		var str = "";
		args.do({|a|
			str = str + a;
		});
		str.postln;
	}
}
TRIG { // useful to map a unipolar function to lo and hi
	*new {
		arg x,lo=0,hi=1;
		^RNG( 0.5 + (0.5*x), lo,hi);
	}
}
BUTTON { // maps a given MIDI button to a function, with optional width/height of button selection
	*new {
		arg x,y,func,w=1,h=1;

		w.do({|i|
			h.do({|j|
				~q.getMidiDict[\buttons][y+j][x+i] = func;
			});
		});
	}
}
SYNTH { // makes a Synth, so Synth(\s, [\n,60], addAction:0) == SYNTH(\s,\n,60,\action,0)
	*new {
		arg ... args;
		var name, target, action;

		name = args[0];
		args.removeAt(0);

		if( args.includes(\target) ){
			var index = args.indexOf(\target);
			target = args[index+1];
			args.removeAt(0);
			args.removeAt(0);
		};
		if( args.includes(\action) ){
			var index = args.indexOf(\action);
			action = args[index+1];
			args.removeAt(0);
			args.removeAt(0);
		};

		if( (target==nil)&&(action==nil) ){
			Synth(name, args);
		}{
			if( (target!=nil)&&(action!=nil) ){
				Synth(name, args, target, action);
			}{
				if(target==nil){
					Synth(name, args, addAction:action);
				} {
					Synth(name, args, target:target);
				};
			};
		};

	}
}