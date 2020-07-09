(

//################### SYNTHS

SynthDef(\s, { // basic sine + saw
	arg bus=0, n=60, a=0, r=0.1, re=1,l=10000, p=0, sin=1, saw=1,v=1;
	var env,sig;
	var freq = n.midicps;
	env = EnvGen.ar( Env.perc(a,r), doneAction:2 );
	sig = Pan2.ar(SinOsc.ar(freq,sin)+Saw.ar(freq,saw) , p);
	sig = Compander.ar(sig,thresh:1);
	sig = RLPF.ar(sig, l,re);
	Out.ar(bus, sig*env*v );
}).add;

SynthDef(\x, { // wibbly synth
	arg bus=0, n=60, a=0, r=0.5, re=1,l=1,lm=10000, p=0, v=1;
	var env,sig, lenv;
	var freq = n.midicps;
	env = EnvGen.ar( Env.perc(a,r), doneAction:2 );
	lenv = SinOsc.kr((a+r)*l).range(100,lm);
	sig = DPW4Saw.ar(freq);
	sig = RLPF.ar(sig, lenv,re);
	sig = sig + FreeVerb.ar(Pulse.ar(freq*2), 0.3, 0.8);
	sig = Pan2.ar(sig, p);
	Out.ar(bus, sig*env*v );
}).add;

SynthDef(\sn, { // snare
	arg bus=0, n=40, a=0, r=0.05, re=1,l=10000, p=0, v=1;
	var env,sig;
	var freq = n.midicps;
	env = EnvGen.ar( Env.perc(a,r), doneAction:2 );
	sig = WhiteNoise.ar(1) - SinOsc.ar( freq * EnvGen.ar(Env.linen(r/2,0,r/2,2))  );
	sig = Pan2.ar( sig, p);
	sig = RLPF.ar(sig, l,re);
	Out.ar(bus, sig*env*v );
}).add;

SynthDef(\lob, { // low bass/drum
	arg bus=0, n=55, a=0, r=0.5, re=1,l=1000, p=0, v=1, flev=3;
	var env,env2,sig;
	var freq = n.midicps;

	env = EnvGen.ar( Env.linen(0,0.0,r), doneAction:2 );
	env2 = EnvGen.ar( Env.perc(0,r,flev) );

	sig = SinOsc.ar(freq*env2);
	sig = Pan2.ar( sig, p);
	sig = BPeakEQ.ar(sig, freq*0.35, 0.5, 16);
	sig = RLPF.ar(sig, l,re);
	Out.ar(bus, sig*env*v );
}).add;

//################### FX

SynthDef(\s_m, {
	arg bus=0, n=60, a=0, r=0.1, re=1,l=10000, p=0, sin=1, saw=0.5,v=1;
	var sig;
	var freq = n.midicps;
	sig = Pan2.ar(SinOsc.ar(freq,sin)+Saw.ar(freq,saw) , p);
	sig = Compander.ar(sig,thresh:1);
	sig = RLPF.ar(sig, l,re);
	Out.ar(bus, sig*v );
}).add;

SynthDef(\del, {
	arg bus=0,t=0.125,c=2,v=1;
	var in = In.ar(bus,2);
	in = CombC.ar(in, 0.2, t, c, v);
	Out.ar(0, in);
}).add;

SynthDef(\deci, {
	arg bus=0,r=44100,bt=24;
	var in = In.ar(bus,2);
	in = Decimator.ar(in,r,bt);
	ReplaceOut.ar(bus, in);
}).add;

SynthDef(\verb, {
	arg bus=0, r=1,m=0.3;
	var in = In.ar(bus,2);
	in = FreeVerb.ar(in, m, r);
	Out.ar(bus, in);
}).add;

SynthDef(\out, {
	arg bus=0;
	var in = In.ar(bus,2);
	Out.ar(0, in);
}).add;

//################### SAMPLERS

SynthDef(\smp, {
	arg buf,bus=0,ra=1,po=0,l=10000,v=0.5,p=0,a=0,s=1,r=0,lo=0;
	var env;
	var sig = PlayBuf.ar(2,buf,
		rate: BufRateScale.ir(buf) * ra,
		startPos: BufDur.kr(buf)*44100*po,
		loop: lo
	);
	env = EnvGen.ar( Env.linen(a,s,r), doneAction:2 );
	sig = Mix.ar(sig);
	sig = LPF.ar(sig,l);
	sig = Pan2.ar(sig,p,v*env);
	Out.ar(bus, sig);
}).add;

SynthDef(\loop, {
	arg buf,bus=0,ra=1,po=0,l=10000,v=1,p=0,lo=1,t=0;
	var env;
	var sig = PlayBuf.ar(2,buf,
		rate: BufRateScale.ir(buf) * ra,
		startPos: BufDur.kr(buf)*44100*po,
		trigger: t,
		loop: lo
	);
	sig = Mix.ar(sig);
	sig = LPF.ar(sig,l);
	sig = Pan2.ar(sig,p,v);
	Out.ar(bus, sig);
}).add;

//################### KR

SynthDef(\sine, {
	arg bus=0, freq=0.5, phase=0;
	Out.kr(bus, SinOsc.kr(freq,phase) );
}).add;

SynthDef(\cos, {
	arg bus=0, freq=0.5, phase=(pi/2);
	Out.kr(bus, SinOsc.kr(freq,phase) );
}).add;

SynthDef(\nstep, {
	arg bus=0, freq=0.5;
	Out.kr(bus, LFNoise0.kr(freq).range(-1,1) );
}).add;

SynthDef(\nramp, {
	arg bus=0, freq=0.5;
	Out.kr(bus, LFNoise1.kr(freq).range(-1,1) );
}).add;

)