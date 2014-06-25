#!/usr/bin/env bash

storedir=/tmp
filename=chess.dat

absme=`readlink -f $0`
abshere=`dirname $absme`

if [ ! -f $storedir/$filename ]; then
  cd $storedir
  wget http://fimi.ua.ac.be/data/chess.dat
fi

cd $abshere/../../src

pwd
javac ToBoolMatrix.java
java ToBoolMatrix `cygpath -wp $storedir/$filename` > `cygpath -wp $storedir/$filename.transf`
