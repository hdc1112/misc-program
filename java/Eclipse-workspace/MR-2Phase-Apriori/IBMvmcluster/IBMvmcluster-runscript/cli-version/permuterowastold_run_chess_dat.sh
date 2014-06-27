#!/usr/bin/env bash

set -x

if [ $# -lt 2 ]; then
  echo Usage `basename $0` permute.file tolerate [noreupload]
  exit 1
fi

# third parameter could be "noreupload"

absme=`readlink -f $0`
abshere=`dirname $absme`

storedir=/tmp
filename=chess.dat
transf=$storedir/$filename.transf

cd $abshere
pwd

if [ ! -f $transf ]; then
  echo did not find $transf, transform now
  ./transform_chess.sh
elif [ `cat $storedir/$filename | wc -l` != `cat $transf | wc -l` ]; then
  echo found $transf, but it is corrupted
  ./transform_chess.sh
else
  echo found $transf, it looks good
fi

# now we have chess.dat

filenameastold=$transf-permuteastold

permutefile=$1
permutefile=`readlink -f $permutefile`

cd $abshere/../../../src
javac PermuteRowsAsTold.java
java PermuteRowsAsTold `cygpath -wp $transf` `cygpath -wp $permutefile`
cd $abshere

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

./run.sh /tmp/tempdatafolder $columns 70 $2 $3
date

set +x
