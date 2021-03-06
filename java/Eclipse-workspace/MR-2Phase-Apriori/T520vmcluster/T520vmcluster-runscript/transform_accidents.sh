#!/usr/bin/env bash

storedir=/tmp
filename=accidents.dat

absme=`readlink -f $0`
abshere=`dirname $absme`

if [ ! -f $storedir/$filename ]; then
  cd $storedir
  wget http://fimi.ua.ac.be/data/accidents.dat
  cd $abshere
else
  cd $abshere
fi

pwd
javac ToBoolMatrix.java
java ToBoolMatrix `cygpath -wp $storedir/$filename` > `cygpath -wp $storedir/$filename.transf`
