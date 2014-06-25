#!/usr/bin/env bash

set -x

if [ $# -lt 1 ]; then
  echo Usage permute.file
  exit 1
fi

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
# now we have chess.dat

filenameastold=$transf-permuteastold

permutefile=$1
permutefile=`readlink -f $permutefile`

javac PermuteRowsAsTold.java
java PermuteRowsAsTold `cygpath -wp $transf` `cygpath -wp $permutefile`

totallinenum=`cat $storedir/$filename | wc -l`
linenum=$totallinenum
#linenum=$((linenum/2))
#linenum=$((linenum/2))
#linenum=$((linenum/2))

realfile=$filenameastold

halflinenum=$((linenum/2))
columns=`head -1 $transf | awk '{print NF}'`
echo columns=$columns

rm -rf /tmp/tempdatafolder/*
cat $realfile | head -n $halflinenum > /tmp/tempdatafolder/1.txt
cat $realfile | head -n $linenum | tail -n $halflinenum > /tmp/tempdatafolder/2.txt

diff -q /tmp/tempdatafolder/1.txt /tmp/tempdatafolder/2.txt
#diff -s /tmp/tempdatafolder/1.txt /tmp/tempdatafolder/2.txt

./run.sh /tmp/tempdatafolder $columns 70 $2
date

set +x
