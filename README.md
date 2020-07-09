## q
A Supercollider library to help speed up livecoding, as well as some utilities for audio-visual livecoding

##### CONTENTS
* [Installation](#installation)
* [Extras](#extras)
* [QAV](#qav)
```SuperCollider

~q = Q(s) // creates Q, optionally you can use Q(s,true) if you want to enable MIDI at startup.
// you can also use ~q.loadMidi to start midi after initialisation

BPM(150) // sets default BPM

// this creates a Pbind called NAME, where NAME is a symbol (i.e, \melody)
( DF( NAME, B(\s,
	\n,DE( S(0,RX(5,6,7,\r,1),5,\st,2) ,4),
	\v,1,\p,0,\a,0,\r,S(0.1,0.2,0.5),
	\dur,8,\of,0) // dur is divided by 8, i.e, dur == 8 means dur = 1 beats, dur == 4 means dur = 0.5 beats
) )
DF( NAME ); // this stops the Pbind called NAME

// This is "equivalent" to ...
Pdef( NAME, Pbind(\instrument,\s, // i.e, in B the first argument is always the synthdef (instrument)
	\degree,Pstutter(2, Pseq([0, Pxrand([5,6,7],1), 5],inf) ),
	\octave,4,
	/* other synth args */,
	\dur,1,
	\timingOffset,0)
).play;

```

# In more detail ...
* B represents a Pbind
* M represents a Pmono
* S represents a Pseq
* R,RX,RW are Prand, Pxrand, Prwand

## Certain key symbols do things to that sequence:
 Some, like \st, embed the sequence into another:
 I.e, S(0,3,5,\st,4) represents Pstut(4, Pseq([0,3,5],inf) ), so \st for "stutter".
 \r represents the "repeat" argument for a sequence, so S(0, R(1,2,3,\r,1), 4) would give 0, followed by a random number from [1,2,3], followed by 4, and so on. Whereas S(0, R(1,2,3), 4) would give 0, followed by a random number from [1,2,3], followed by another random number, followed by another ... etc, forever (because it has /r = inf by default).

## There are also key symbols for Pbinds:
* \cl embeds the Pbind into a Pclutch, so if \cl = 0, the sequence will not increase its position (just repeat the last set of values it received).
* Some are just shorthand, for instance \of just becomes \timingOffset
* Some are more complex. "\fx,FX(\delay)" for instance, gets converted to "\bus,FX(\delay).b,\group,FX(\delay).g" (*see FX)

## For more functions, look at QFUNCS

# Installation
Execute ```Platform.userExtensionDir``` to find your extension folder, then extract this archive into a folder called "q". You may also need SC3-Plugins and DDWSnippets, as well as BenoitLib.

# Extras:

## Samples:
Q create a sample dictionary using samples located in "Documents/SuperCollider/Samples" it expects to find a bunch of folders, i.e, kick, snare, etc. Each containing a bunch of wav files. To change this directory, manually edit ~q.loadSamples (i'll fix this soon maybe ;) )

```SuperCollider
(
DF(NAME, B(\smp,
	\buf,D(FOLDER,SAMP), // folder should be a symbol representing the folder name, and sample a number
	\v,1,\p,0,\a,0,\r,0,\s,1,\po,0,\ra,2, // po = position the sample plays from, ra = rate of playback
	\dur,8,of,0,
));
)
// FOLDER and SAMP can also be sequences!

(
DF(NAME, M(\loop, // use the loop synth when using M
	\buf,D(FOLDER,SAMP),
	\v,1,\p,0,\po,0,\ra,2,\t,0, // t == 0 means that the audio wont restart every \dur, t == 1 will restart
	\dur,8
));
)
```

## MIDI:
Warning: This has been setup with a nanoKONTROL2 on Linux, so the cc numbers of the sliders will vary massively with any other midi device. Manually edit ~q.loadMidi to setup your own device! Either use ~q = Q(s,true) to setup MIDI, or use "~q = Q(s); ~q.loadMidi"

```SuperCollider
~q = Q(s,true);

(
DF(\melody, B(\s,\n,DE(0,4),
	\v,1,\p,0,\a,0,
	\r,MIDI(\k0,1,4), // get value of k0 (knob 0) and maps it to range 1 <-> 4
	\dur,8,\of,0
));
DF(\melody);
)

MIDIV(\k0) // this returns the value of knob 0 as a float

// this sets the buttons [0,0],[0,1],[0,2],[1,0],[1,1],[1,2] to the given function (in this case a synth)
// the function takes two arguments, corresponding to x and y coord of button
BUTTON(0,0, {|x,y| SYNTH(\s, \n,DE(x,y+3),\r,MIDIV(\k0,0.1,2) ) }, w:3,h:2)
```

## FX routing

```SuperCollider
FX( FX_GROUP ) // this will create a unique bus number and create a group under the given name

(
DF(FX_NAME, M(FX_NAME, // this is just a Pmono (Pbind for a continuous synth)
	\dur,4,
	\fx,FX(FX_GROUP) // this automatically sets the output bus the given FX bus, and sets the group to the FX group
));
// DF(NAME);
);

(
DF(NAME, B(\s,\n,DE(0,4),
	\v,1,\p,0,\a,0,\r,0.5,
	\dur,8,\of,0,
	\fx,FX(FX_GROUP) // this makes NAME output to the required FX group
));
// DF(NAME);
)
```

## KR Busses:
This requires BenoitLib's Pkr extension!

```SuperCollider
// This creates a unique bus number for BUS_NAME, and adds a Synth to it (using the synthdef name)
BUS( BUS_NAME, BUS_SYNTHDEF, \t,4, \x,1,\y,3,\z,4, ... ) // \t gets converted to beats (rather than seconds)

(
DF(NAME, B(\s,\n,DE(0,4),
	\v,1,\p,0,\a,0,\r, MAP( KR(BUS_NAME), -1, 1, 0.2, 0.8), // this maps the output of the BUS to the range 0.2,0.8
	\dur,8,\of,0
));
// DF(NAME);
)

BUS(BUS_NAME) // this clears the Synth from the Bus

```

# QAV
QAV is another tool to help speed up livecoding, this time with visuals.

## INIT
This sets up ~q with midi, and sets ~w as our window.
```SuperCollider
~q = Q(s,true);
~w = QAV(500,500, GreyColor(0) ); // w,h, background color
```
## VARIABLES
```SuperCollider
~x = 300; // for example
```
## OBJECTS
You can't make actual Objects on the fly in SuperCollider, but you can simply create an IdentityDictionary containing all the variables and methods as you usually would! In this example I use ObjectArray to create an array of objects (cryptic, I know). You specify the "constructor" of your object (in this case, ~rgbF) with ```~rgb = ObjectArray(~rgbF)```, and then you call ```~rgb.add( ... )``` to add new objects to the array. Use ```~rgb.draw``` to iterate over the array and draw each of the objects, this is just shorthand for ```~rgb.array.do({|o| o[\draw].value }).```.
```SuperCollider
(
~rgbF = { // this is an "add function" which creates a new "object"
	arg x,y,r,radius=4,dur=10; // these are the "constructor arguments"
	var o = (); // o is the IndentityDictionary which is our "object"
	var delay = 25; // other initial variables which start as constant through all of the objects
	var death_dur = 10;
	var sx = x, sy = y;

	// putting the constructor arguments into the "object"
	o[\x] = x; o[\y] = y; o[\r] = r;
	o[\radius] = radius;
	o[\delay] = delay;

	// putting extra variables into the object
	o[\th] = rrand(0.0,2*pi);
	o[\sp] = rrand(0.0,0.1);
	o[\col] = Color.hsv(rrand(0.0,1),1,1);
	o[\dur] = dur;
	o[\life] = 0;
	o[\death] = 0;

	o[\kill] = false; // if o[\kill] = true, then this object will be removed from the set new draw
	o[\draw] = {|f| // this function is called every draw, and takes f = frame as argument
		var sp = o[\sp];
		var radius = o[\radius];
		o[\col] = Color.hsv( MAP(sin(o[\th])),1,1);
		o[\x] = sx + ( o[\r] * cos(o[\th]) );
		o[\y] = sy + ( o[\r] * sin(o[\th]) );

		o[\delay].do({|i|
			var th = o[\th] + (sp*i);
			var col = Color.hsv( MAP(sin(th)) ,1 ,1);
			var x = sx + ( o[\r] * cos(th) );
			var y = sy + ( o[\r] * sin(th) );
			Pen.fillColor = col;
			Pen.fillOval( CenterSquare(x,y,radius) );

		});
		o[\th] = o[\th] + sp;

		if ( o[\life] > o[\dur] ){
			o[\death] = o[\death]+1;
			o[\delay] = o[\delay]-1;
			if( o[\delay] == 0 ){ o[\kill] = true };
		}{
			o[\life] = o[\life]+1;
		}

	};

	o};
~rgb = ObjectArray(~rgbF); // this sets ~rgb to be an array containing our objects

~res = 16;
~res.do({|i|
	var p = i / ~res;
	~rgb.add( // this simply calls the "add_func" associated to the ObjectArray (in this case, ~rgbF)
		~w.w/2,
		~w.h/2,
		200*RNG(p,0.2,1),
		RNG(1-p, 0,40),
		// rrand(60,60*8)
		inf
	);
});
)
```
## DRAW
Change ~draw on the fly to edit the draw function
```SuperCollider
(
~draw = {|f|

	Pen.smoothing = false;

	~rgb.draw;

	~w.clear; // this clears the view after every call
};
)
```
## MANUAL EDITING
```SuperCollider
( // run this to add an object!
~rgb.add(
	~w.w/2,
	~w.h/2,
	rrand(50,200),
	rrand(4, 16),
	rrand(60,60*2)
);
)
```
## MIDI
```SuperCollider
(
BUTTON(0,0, {|x,y| // you can map a button function to do whatever you want!
	var p = (x+(y*8)) /(8*3);
	~rgb.add(
		~w.w/2,
		~w.h/2,
		200*RNG(p,0.2,1),
		RNG(1-p, 0,40),
		rrand(60,60*8)
	);
},8,3);
)
```
You could also add MIDIV(\k0) to your draw function to edit parameters using a midi knob!


