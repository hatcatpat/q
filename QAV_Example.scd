// INIT
~q = Q(s,true);
~w = QAV(500,500, GreyColor(0) ); // w,h, background color

// VARIABLES
~x = 300; // for example

// OBJECTS
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

// DRAW
(
~draw = {|f|

	Pen.smoothing = false;

	~rgb.draw;

	~w.clear; // this clears the view after every call
};
)

// MANUAL
( // run this to add an object!
~rgb.add(
	~w.w/2,
	~w.h/2,
	rrand(50,200),
	rrand(4, 16),
	rrand(60,60*2)
);
)
// MIDI
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
// You could also add MIDIV(\k0) to your draw function to edit parameters using a midi knob!


