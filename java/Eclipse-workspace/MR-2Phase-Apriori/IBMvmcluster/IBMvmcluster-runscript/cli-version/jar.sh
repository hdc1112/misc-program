#!/usr/bin/env bash

# default value stage
worknode=ibmvm1
user=dachuan

# definition, parsing, interrogation stages
while getopts ":w:u:" o; do
  case $o in
    o)
      worknode=$OPTARG
      ;;
    u)
      user=$OPTARG
      ;;
    *)
      echo invalid argument >&2
      exit 1
      ;;
  esac
done

# arguments show stage
echo worknode=$worknode
echo user=$user

# verify arguments stage (skip)

set -x

absme=`readlink -f $0`
abshere=`dirname $absme`

cd $abshere/../../../src

if [ ! -f hadoopclasspath.txt ]; then
  find /home/tempid/hadoop-2.2.0/share/hadoop -type f -name "*.jar" | $abshere/concatenate.sh > hadoopclasspath.txt
fi
classes=`cat hadoopclasspath.txt`
javac -classpath $classes *.java
jar cvfe ./mr-2phase-apriori.jar MR2PhaseApriori *.class
jar -tf ./mr-2phase-apriori.jar
scp ./mr-2phase-apriori.jar $user@$worknode:/tmp/
rm -f *.class

set +x
