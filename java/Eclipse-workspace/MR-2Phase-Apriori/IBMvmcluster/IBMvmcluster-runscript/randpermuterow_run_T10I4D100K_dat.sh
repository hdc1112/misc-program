#!/usr/bin/env bash

set -x

# first parameter could be "noreupload"

absme=`readlink -f $0`
abshere=`dirname $absme`

storedir=/tmp
filename=T10I4D100K.dat
transf=$storedir/$filename.transf

cd $abshere

if [ ! -f $transf ]; then
  ./transform_T10I4D100K.sh
fi

totallinenum=`cat $storedir/$filename | wc -l`
linenum=$totallinenum
#linenum=$((linenum/2))
#linenum=$((linenum/2))
#linenum=$((linenum/2))

realfile=$transf.realfile
cat $transf | head -n $linenum > $realfile

cd $abshere/../../src
javac PermuteRows.java
java PermuteRows `cygpath -wp $realfile`
cd $abshere

realfile=${realfile}-randpermute

halflinenum=$((linenum/2))
columns=`head -1 $transf | awk '{print NF}'`

rm -rf /tmp/tempdatafolder/*
if [ ! -d /tmp/tempdatafolder ]; then
  mkdir /tmp/tempdatafolder
fi
cat $realfile | head -n $halflinenum > /tmp/tempdatafolder/1.txt
cat $realfile | head -n $linenum | tail -n $halflinenum > /tmp/tempdatafolder/2.txt

diff -q /tmp/tempdatafolder/1.txt /tmp/tempdatafolder/2.txt
#diff -s /tmp/tempdatafolder/1.txt /tmp/tempdatafolder/2.txt

./run.sh /tmp/tempdatafolder $columns 0.52 $1
date

set +x
