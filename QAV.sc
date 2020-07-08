QAV{

	var window, view, width, height, sc, draw;

	*new {
		arg width_=600, height_=600, bg=Color.white;
		^super.new.init(width_,height_,bg);
	}
	init {
		arg width_,height_,bg;

		sc = 1;

		width = width_;
		height = height_;

		~draw = { arg f; };

		window = Window("", Rect(99, 99, width, height), false, false);
		view = UserView(window, Rect(0,0,width,height) );
		window.background = bg;

		//--main loop
		view.drawFunc = {
			this.margin(sc);
			~draw.value(view.frame);
		};
		view.keyDownAction = {
			arg view,char,modifiers,unicode,keycode;
			switch(char.asString,
				"q", { window.close }
			);
		};

		//--window management
		view.clearOnRefresh = false;
		view.background = bg;
		window.onClose = {};
		window.alwaysOnTop = true;
		window.front;
		view.animate = true;
		CmdPeriod.doOnce({if(window.isClosed.not, {window.close})});

	}

	clear {
		view.clearDrawing;
	}
	margin {
		arg m;
		Pen.translate(width/2, height/2);
		Pen.scale(m,m);
		Pen.translate(-1*width/2, -1*height/2);
	}
	bg{
		arg col;
		window.background = col;
		view.background = col;
	}
	w{ ^width; }
	h{ ^height; }
	v{ ^view; }
	wd{ ^window; }
	sc{ arg sc_; sc = sc_; }

	// useful funcs

	sin {
		arg theta, lo=(-1), hi=1;
		var x = 0.5 + (sin(theta)*0.5);
		x = x * (hi - lo);
		x = x + lo;
		^x;
	}
	cos {
		arg theta, lo=(-1), hi=1;
		var x = 0.5 + (cos(theta)*0.5);
		x = x * (hi - lo);
		x = x + lo;
		^x;
	}
}

CenterRect {
	*new {
		arg x,y,w,h;
		^Rect( x-(w/2), y-(h/2), w, h );
	}
}

CenterSquare {
	*new {
		arg x,y,r;
		^CenterRect(x,y,r,r);
	}
}

GreyColor {
	*new {
		arg c,a=1;
		^Color(c,c,c,a);
	}
}