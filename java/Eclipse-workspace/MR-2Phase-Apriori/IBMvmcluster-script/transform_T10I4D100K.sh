#!/usr/bin/env bash

storedir=/tmp
filename=T10I4D100K.dat

absme=`readlink -f $0`
abshere=`dirname $absme`

if [ ! -f $storedir/$filename ]; then
  cd $storedir
  wget http://fimi.ua.ac.be/data/T10I4D100K.dat
  cd $abshere
else
  cd $abshere
fi

javac ToBoolMatrix.java
java ToBoolMatrix `cygpath -wp $storedir/$filename` > `cygpath -wp $storedir/$filename.transf`
