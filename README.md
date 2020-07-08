# q
A Supercollider library to help speed up livecoding, as well as some utilities for audio-visual livecoding

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

// In more detail ...
// B represents a Pbind
// M represents a Pmono
// S represents a Pseq
// R,RX,RW are Prand, Pxrand, Prwand

// Certain key symbols do things to that sequence:
//// Some, like \st, embed the sequence into another:
//// I.e, S(0,3,5,\st,4) represents Pstut(4, Pseq([0,3,5],inf) ), so \st for "stutter".
//// \r represents the "repeat" argument for a sequence, so S(0, R(1,2,3,\r,1), 4) would give 0, followed by a random
//// number from [1,2,3], followed by 4, and so on. Whereas S(0, R(1,2,3), 4) would give 0, followed by a random number
//// from [1,2,3], followed by another random number, followed by another ... etc, forever (because it has /r = inf by
//// default).

// There are also key symbols for Pbinds:
//// \cl embeds the Pbind into a Pclutch, so if \cl = 0, the sequence will not increase its position (just repeat the last
//// set of values it received).
//// Some are just shorthand, for instance \of just becomes \timingOffset
//// Some are more complex. "\fx,FX(\delay)" for instance, gets converted to "\bus,FX(\delay).b,\group,FX(\delay).g" (*see FX)

// For more functions, look at QFUNCS

// Extras:

//// Samples:
// Q create a sample dictionary using samples located in "Documents/SuperCollider/Samples"
// it expects to find a bunch of folders, i.e, kick, snare, etc. Each containing a bunch of wav files.
// To change this directory, manually edit ~q.loadSamples (i'll fix this soon maybe ;) )

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

//// MIDI:
// Warning: This has been setup with a nanoKONTROL2 on Linux, so the cc numbers of the sliders will vary massively with
// any other midi device. Manually edit ~q.loadMidi to setup your own device!
// Either use ~q = Q(s,true) to setup MIDI, or use "~q = Q(s); ~q.loadMidi"

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

//// FX routing

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

//// KR Busses:
// This requires BenoitLib's Pkr extension!

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
