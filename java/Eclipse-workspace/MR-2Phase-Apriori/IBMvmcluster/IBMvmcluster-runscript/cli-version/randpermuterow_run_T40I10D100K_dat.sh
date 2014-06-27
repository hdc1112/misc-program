#!/usr/bin/env bash

set -x

# first parameter could be "noreupload"

absme=`readlink -f $0`
abshere=`dirname $absme`

storedir=/tmp
filename=T40I10D100K.dat
transf=$storedir/$filename.transf

cd $abshere
pwd

if [ ! -f $transf ]; then
  echo did not find $transf, transform now
  ./transform_T40I10D100K.sh
elif [ `cat $storedir/$filename | wc -l` != `cat $transf | wc -l` ]; then
  echo found $transf, but it is corrupted
  ./transform_T40I10D100K.sh
else
  echo found $transf, it looks good
fi

totallinenum=`cat $storedir/$filename | wc -l`
echo totallinenum=$totallinenum
linenum=$totallinenum
linenum=$((linenum/2))
linenum=$((linenum/2))
linenum=$((linenum/2))
linenum=$((linenum/2))
echo linenum=$linenum

realfile=$transf.realfile
cat $transf | head -n $linenum > $realfile

javac PermuteRows.java
java PermuteRows `cygpath -wp $realfile`

realfile=${realfile}-randpermute

halflinenum=$((linenum/2))
columns=`head -1 $transf | awk '{print NF}'`

rm -rf /tmp/tempdatafolder/*
cat $realfile | head -n $halflinenum > /tmp/tempdatafolder/1.txt
cat $realfile | head -n $linenum | tail -n $halflinenum > /tmp/tempdatafolder/2.txt

diff -q /tmp/tempdatafolder/1.txt /tmp/tempdatafolder/2.txt
#diff -s /tmp/tempdatafolder/1.txt /tmp/tempdatafolder/2.txt

#minsupport=1
#./run.sh /tmp/tempdatafolder $columns 1 $1
#minsupport=0.52
./run.sh /tmp/tempdatafolder $columns 0.52 $1
date

set +x
