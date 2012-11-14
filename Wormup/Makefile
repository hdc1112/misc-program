.PHONY: deflate

zlib.x: zpipe_profile.c
	gcc -o zlib.x zpipe_profile.c -lz

deflate:
	cat ./1.txt | ./zlib.x > 1.compress 2>1.log

deflate-strace:
	cat ./1.txt | strace -tt -o strace.log ./zlib.x >1.compress 2>1.log 
