QAV{ // MAIN WINDOW

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

ObjectArray { // TOOL FOR MAKING ARRAYS OF CUSTOM "OBJECTS"

	var add_func,kill, objs;

	*new {
		arg add_func_, kill_=true;
		^super.new.init(add_func_, kill_);
	}
	init {
		arg add_func_, kill_=true;
		add_func = add_func_;
		kill = kill_;

		objs = [];
	}
	add {
		arg ... args;
		objs = objs ++ [add_func.value(*args)];
		^objs;
	}
	draw {
		arg ... args;
		objs.do({|o|
			o[\draw].value(*args)
		});

		if( kill == true ){
			objs.do({|o|
				if (o[\kill] == true){
					objs.remove(o);
				}
			});
		}
	}
	remove {
		arg ... args;
		objs.removeAll(args);
	}
	array { ^objs; }
	setAddFunc { arg func; add_func = func; }
}

VIDEO {
	var <>images,<>pos,<>sp,region;

	*new {
		arg start,length,folder_name,video=nil;
		if (video == nil){
			^super.new.initFromFolder(start,length,folder_name);
		}{
			^super.new.initFromVIDEO(start,length,video);
		}
	}
	initFromFolder {
		arg start,length,folder_name;
		var start_frames,length_frames;
		var files = PathName(folder_name).files;

		start_frames = floor( (files.size)*start);
		length_frames = floor( (files.size)*(1-start)*length);

		images = Array.fill(length_frames);
		length_frames.do({|f|
			PRINT("Loading Frame:",f);
			images[f] = Image.new(files[f+start_frames].fullPath);
		});
		this.init;
	}
	initFromVIDEO {
		arg start,length,video;
		var start_frames,length_frames;

		start_frames = floor( (video.images.size)*start);
		length_frames = floor( (video.images.size)*(1-start)*length);

		images = Array.fill(length_frames);
		length_frames.do({|f|
			images[f] = video.images[f+start_frames];
		});

		this.init;
	}
	init {
		pos = 0;
		sp = 1;
		region = 0@1;
	}
	draw {
		arg rect, fromRect=nil, operation= 'sourceOver', fraction=1.0;

		images[pos+floor(region.x * (images.size) )].drawInRect(rect,fromRect,operation,fraction);

		pos = pos + sp;
		pos = ( pos % floor(region.y * (1-region.x) * (images.size) ) );
	}
	region_ {
		arg r;
		if (r.x >= 1){ r.x = 1 };
		if (r.y >= 1){ r.y = 1 };
		region = r;
		pos = 0;
	}
}

// UTILS
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