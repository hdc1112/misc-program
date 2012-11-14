# normal version
zlib.x: zpipe.c
	gcc -o zlib.x zpipe.c -lz

# source code instru version
zlib.x.si: zpipe_sourceinstru.c
	gcc -o zlib.x.si zpipe_sourceinstru.c -lz

.PHONY: deflate deflate-si deflate-strace1 deflate-strace2 deflate-strace3 deflate-strace4

# normal use
deflate: zlib.x
	cat ./1.txt | ./zlib.x > 1.compress 2>1.log

deflate-si: zlib.x.si
	cat ./1.txt | ./zlib.x.si > 1.compress 2>1.log

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
