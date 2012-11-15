# normal version
zlib.x: zpipe.c
	gcc -o zlib.x zpipe.c -lz

# source code instru version
zlib.x.si: zpipe_sourceinstru.c
	gcc -o zlib.x.si zpipe_sourceinstru.c -lz

zlib.x.noprefetch: zpipe_noprefetch.c
	gcc -o zlib.x.noprefetch zpipe_noprefetch.c -lz

.PHONY: clean deflate deflate-si inflate deflate-strace1 deflate-strace2 deflate-strace3 deflate-strace4 \
	deflate-strace5 deflate-strace6 deflate-strace7 \
	deflate-strace8 deflate-strace9 deflate-strace10

clean:
	rm -f 1.compress;
	rm -f 1.log;
	rm -f strace.log;
	rm -f 1.txt.duibi;
	find -type f -name "*~" | xargs rm -f;
	find -type f -name "zlib.x*" | xargs rm -f;
	find -type f -name "a.out" | xargs rm -f;
	find -type f -name "a.exe" | xargs rm -f;

# normal use
deflate: zlib.x
	cat ./1.txt | ./zlib.x > 1.compress 2>1.log

deflate-si: zlib.x.si
	cat ./1.txt | ./zlib.x.si > 1.compress 2>1.log

inflate: zlib.x
	cat ./1.compress | ./zlib.x -d > 1.txt.duibi

# strace for zpipe.c's deflate
# (warning)
# strace.log is ignored by git
deflate-strace1: zlib.x
	cat ./1.txt | strace -tt -o ./strace.log ./zlib.x >1.compress 2>1.log 

deflate-strace2: zlib.x
	cat ./1.txt | strace -r -o ./strace.log ./zlib.x >1.compress 2>1.log 

# strace for zpipe_sourceinstru.c's deflate
deflate-strace3: zlib.x.si
	cat ./1.txt | strace -tt -o ./strace.log ./zlib.x.si >1.compress 2>1.log 

deflate-strace4: zlib.x.si
	cat ./1.txt | strace -r -o ./strace.log ./zlib.x.si >1.compress 2>1.log 

deflate-strace5: zlib.x.noprefetch
	cat ./1.txt | strace -tt -o ./strace.log ./zlib.x.noprefetch >1.compress 2>1.log

deflate-strace6: zlib.x.noprefetch
	cat ./1.txt | strace -r -o ./strace.log ./zlib.x.noprefetch >1.compress 2>1.log

deflate-strace7: zlib.x
	cat ./1.txt | strace -T -o ./strace.log ./zlib.x >1.compress 2>1.log 

deflate-strace8: zlib.x
	cat ./1.txt | strace -T -tt -o ./strace.log ./zlib.x >1.compress 2>1.log 

deflate-strace9: zlib.x.noprefetch
	cat ./1.txt | strace -T -o ./strace.log ./zlib.x.noprefetch >1.compress 2>1.log 

deflate-strace10: zlib.x.noprefetch
	cat ./1.txt | strace -T -tt -o ./strace.log ./zlib.x.noprefetch >1.compress 2>1.log 
