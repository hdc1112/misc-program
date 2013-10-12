# example file hellow/Makefile
CC = gcc
INCLUDES = -I/usr/include/mysql
LIBS = -lmysqlclient

all:hellow

test-1.x: test-1.c
	$(CC) -o test-1.x test-1.c $(INCLUDES) $(LIBS)

clean:
	rm -rf *.o
	rm -rf *.x
	rm -rf *~
