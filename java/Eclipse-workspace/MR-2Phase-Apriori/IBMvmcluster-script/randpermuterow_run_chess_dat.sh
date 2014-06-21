#!/usr/bin/env bash

# this script doesn't log anything to file
# use 2>&1 | tee /tmp/abc.log by yourself

set -x

# first parameter could be "noreupload"

absme=`readlink -f $0`
abshere=`dirname $absme`

storedir=/tmp
filename=chess.dat
transf=$storedir/$filename.transf

cd $abshere
pwd

if [ ! -f $transf ]; then
  ./transform_chess.sh
fi

totallinenum=`cat $storedir/$filename | wc -l`
linenum=$totallinenum
#linenum=$((linenum/2))
#linenum=$((linenum/2))
#linenum=$((linenum/2))

realfile=$transf.realfile
cat $transf | head -n $linenum > $realfile

cd $abshere/../src/
javac PermuteRows.java
java PermuteRows `cygpath -wp $realfile`
cd $abshere

realfile=${realfile}-randpermute

halflinenum=$((linenum/2))
columns=`head -1 $transf | awk '{print NF}'`
echo columns=$columns

rm -rf /tmp/tempdatafolder/*
if [ ! -d /tmp/tempdatafolder ]; then
  mkdir /tmp/tempdatafolder
fi
cat $realfile | head -n $halflinenum > /tmp/tempdatafolder/1.txt
cat $realfile | head -n $linenum | tail -n $halflinenum > /tmp/tempdatafolder/2.txt

diff -q /tmp/tempdatafolder/1.txt /tmp/tempdatafolder/2.txt
#diff -s /tmp/tempdatafolder/1.txt /tmp/tempdatafolder/2.txt

./run.sh /tmp/tempdatafolder $columns 65 $1
date

set +x
