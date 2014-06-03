#!/usr/bin/env bash

absme=`readlink -f $0`
abshere=`dirname $absme`

cd $abshere

classes=`find /cygdrive/c/cygwin/home/dachuan/hadoop-2.2.0/share/hadoop/ -type f -name "*.jar" | ./concatenate.sh`
javac -classpath $classes *.java
jar cvfe ./mywordcount.jar WordCount *.class
jar -tf ./mywordcount.jar
scp ./mywordcount.jar hadoop1:/tmp/
rm -f *.class
