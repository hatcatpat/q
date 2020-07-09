(

DDWSnippets.put("DF", "(
DF(NAME, B(\\s,\\n,DE(0,4),
\t\\v,1,\\p,0,\\a,0,\\r,0.5,
\t\\dur,8,\\of,0
));
// DF(NAME);
)");

DDWSnippets.put("SMP", "(
DF(NAME, B(\\smp,\\buf,D(FOLDER,SAMP),
\t\\v,1,\\p,0,\\a,0,\\r,0,\\s,1,
\t\\dur,8,\of,0,
));
// DF(NAME);
)");

DDWSnippets.put("LOOP", "(
DF(NAME, M(\\loop,\\buf,D(FOLDER,SAMP),
\t\\v,1,\\p,0,\\t,0,
\t\\dur,8
));
// DF(NAME);
)");

DDWSnippets.put("SYNTHDEF", "
(
SynthDef(NAME, {
	arg bus=0, n=60, a=0, r=0.1, re=1,l=10000, p=0, v=1;
	var env,sig;
	var freq = n.midicps;
	env = EnvGen.ar( Env.perc(a,r), doneAction:2 );
	sig = Pan2.ar( SinOsc.ar(freq), p);
	sig = RLPF.ar(sig, l,re);
	Out.ar(bus, sig*env*v );
}).add;
)
");

DDWSnippets.put("FX","
(
DF(NAME, M(NAME,
\\dur,4,\\fx,FX(NAME)));
// DF(NAME);
);
");

// QAV

DDWSnippets.put("OBJ","
(
~NEWOBJF = {
	arg x,y,r,col;
	var o = (x:x,y:y,r:r,col:col);

	o[\\kill] = false;
	o[\\draw] = {|f|
		PRINT(o,f);
	};

o};
~NEWOBJ = ObjectArray(~NEWOBJF);
)
");

)

