#!/usr/bin/env bash

set -x

# second parameter could be "noreupload"
if [ $# -lt 1 ]; then
  echo Usage `basename $0` tolerate [noreupload]
  exit 1
fi

absme=`readlink -f $0`
abshere=`dirname $absme`

storedir=/tmp
filename=pumsb.dat
transf=$storedir/$filename.transf

cd $abshere
pwd

if [ ! -f $transf ]; then
  ./transform_pumsb.sh
fi

totallinenum=`cat $storedir/$filename | wc -l`
linenum=$totallinenum
#linenum=$((linenum/2))
#linenum=$((linenum/2))
#linenum=$((linenum/2))

realfile=$transf.realfile
cat $transf | head -n $linenum > $realfile

cd $abshere/../../../src
javac PermuteRows.java
java PermuteRows `cygpath -wp $realfile`
cd $abshere

realfile=${realfile}-randpermute

halflinenum=$((linenum/2))
columns=`head -1 $transf | awk '{print NF}'`
echo columns=$columns

rm -rf /tmp/tempdatafolder/*
cat $realfile | head -n $halflinenum > /tmp/tempdatafolder/1.txt
cat $realfile | head -n $linenum | tail -n $halflinenum > /tmp/tempdatafolder/2.txt

diff -q /tmp/tempdatafolder/1.txt /tmp/tempdatafolder/2.txt
#diff -s /tmp/tempdatafolder/1.txt /tmp/tempdatafolder/2.txt

./run.sh /tmp/tempdatafolder $columns 90 $1 $2
date

set +x
