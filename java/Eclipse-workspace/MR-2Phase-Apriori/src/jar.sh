#!/usr/bin/env bash

absme=`readlink -f $0`
abshere=`dirname $absme`

cd $abshere

if [ ! -f hadoopclasspath.txt ]; then
  find /cygdrive/c/cygwin/home/dachuan/hadoop-2.2.0/share/hadoop/ -type f -name "*.jar" | ./concatenate.sh > hadoopclasspath.txt
fi
classes=`cat hadoopclasspath.txt`
javac -classpath $classes *.java
jar cvfe ./mr-2phase-apriori.jar MR2PhaseApriori *.class
jar -tf ./mr-2phase-apriori.jar
scp ./mr-2phase-apriori.jar hadoop1:/tmp/
rm -f *.class
