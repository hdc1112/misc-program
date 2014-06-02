#!/usr/bin/env bash

absme=`readlink -f $0`
abshere=`dirname $absme`

cd $abshere

jar cvfe ./mywordcount.jar WordCount *.class
jar -tf ./mywordcount.jar
scp ./mywordcount.jar hadoop1:/tmp/
