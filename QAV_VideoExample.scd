~w = QAV(500,500);

~sz = 350
~szb = 350
(
~draw = {|f|

	Pen.fillColor = GreyColor(0);
	Pen.fillRect(0,0,~w.w,~w.h);

	a.draw( CenterSquare(~w.w/2,~w.h/2,~w.w)  );
	b.draw( CenterSquare(~w.w/2,~w.h/2,~szb), fraction:MAP(KRV(\sine),-1,1,0.3) );

	~w.clear;
}
)
~draw = {}

a = VIDEO(0.0,1,"Documents/SuperCollider/AudioVisuals/VideoTest/Images")
b = VIDEO(0.0,1,video:a)

a.sp = -1
b.sp = 1
a.region_( 0.3@0.01 )
b.region_( 0.3@0.05 )

BUS(\sine,\sine,\t,T(8))
(
DF(\f1, FUNC(\sw, R(0,1,\st,R(1,4,8) ),\dur,SW(K(\sw), 4,1),
	\start, SW(K(\sw), MAP(KR(\sine)), R(0.1,0.2,0.3,0.4,0.5,\st,R(1,4,8) ) ),
	\len, W(0.01,1),
	\fn, {|e|
		a.region_( e[\start]@e[\len] );
		b.region_( e[\start]@(e[\len]*2) );
		a.sp = rrand(1.0,4.0)*[-1,1].choose;
		b.sp = rrand(1.0,4.0)*[-1,1].choose;
		~sz = rrand(100,250);
		~szb = rrand(100,500);
	}
));
// DF(\f2);
)