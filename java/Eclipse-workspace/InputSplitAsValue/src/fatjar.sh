#!/usr/bin/env bash

absme=`readlink -f $0`
abshere=`dirname $absme`

cd $abshere

if [ ! -f hadoopclasspath.txt ]; then
  find /cygdrive/c/cygwin/home/dachuan/hadoop-2.2.0/share/hadoop/ -type f -name "*.jar" | ./concatenate.sh > hadoopclasspath.txt
fi
classes=`cat hadoopclasspath.txt`
javac -classpath $classes *.java
jar cvfe ./mywordcount.jar WordCount *.class
jar -tf ./mywordcount.jar
scp ./mywordcount.jar hadoop1:/tmp/
rm -f *.class

if [ "$1" = "run" ]; then
  ssh -n hadoop1 /home/dachuan/projects/run.sh
fi
