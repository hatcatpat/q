(
s.options.numBuffers = 1024 * 16;
s.options.memSize = 8192 * 64;

s.options.numOutputBusChannels = 2;
s.options.numInputBusChannels = 0;

s.boot;
s.waitForBoot{
	Task({
		1.wait;
		~q = Q(s);
		"/home/pat/Documents/SuperCollider/blank.scd".openDocument;
	}).start;
}
)

