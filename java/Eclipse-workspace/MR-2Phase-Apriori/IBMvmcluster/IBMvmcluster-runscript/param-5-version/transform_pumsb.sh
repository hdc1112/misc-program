#!/usr/bin/env bash

storedir=/tmp
filename=pumsb.dat

absme=`readlink -f $0`
abshere=`dirname $absme`

if [ ! -f $storedir/$filename ]; then
  cd $storedir
  wget http://fimi.ua.ac.be/data/pumsb.dat
fi

cd $abshere/../../../src

pwd
javac ToBoolMatrix.java
java ToBoolMatrix `cygpath -wp $storedir/$filename` > `cygpath -wp $storedir/$filename.transf`
