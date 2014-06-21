#!/usr/bin/env bash

set -x

absme=`readlink -f $0`
abshere=`dirname $absme`

cd $abshere/../src

if [ ! -f hadoopclasspath.txt ]; then
  find /home/tempid/hadoop-2.2.0/share/hadoop -type f -name "*.jar" | ./concatenate.sh > hadoopclasspath.txt
fi
classes=`cat hadoopclasspath.txt`
javac -classpath $classes *.java
jar cvfe ./mr-2phase-apriori.jar MR2PhaseApriori *.class
jar -tf ./mr-2phase-apriori.jar
scp ./mr-2phase-apriori.jar dachuan@ibmvm1:/tmp/
rm -f *.class

set +x
