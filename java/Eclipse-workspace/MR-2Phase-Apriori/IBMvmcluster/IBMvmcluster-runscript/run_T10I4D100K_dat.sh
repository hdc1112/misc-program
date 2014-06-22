#!/usr/bin/env bash

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
linenum=$((linenum/2))
linenum=$((linenum/2))

realfile=$transf.realfile
cat $transf | head -n $linenum > $realfile


halflinenum=$((linenum/2))
columns=`head -1 $transf | awk '{print NF}'`

rm -rf /tmp/tempdatafolder/*
cat $realfile | head -n $halflinenum > /tmp/tempdatafolder/1.txt
cat $realfile | head -n $linenum | tail -n $halflinenum > /tmp/tempdatafolder/2.txt

./run.sh /tmp/tempdatafolder $columns 1 $1
