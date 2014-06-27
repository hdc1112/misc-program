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
filename=retail.dat
transf=$storedir/$filename.transf

cd $abshere
pwd

if [ ! -f $transf ]; then
  echo did not find $transf, transform now
  ./transform_retail.sh
elif [ `cat $storedir/$filename | wc -l` != `cat $transf | wc -l` ]; then
  echo found $transf, but it is corrupted
  ./transform_retail.sh
else
  echo found $transf, it looks good
fi

totallinenum=`cat $storedir/$filename | wc -l`
linenum=$totallinenum
#linenum=$((linenum/2))
#linenum=$((linenum/2))
#linenum=$((linenum/2))

realfile=$transf.realfile
cat $transf | head -n $linenum > $realfile

javac PermuteRows.java
java PermuteRows `cygpath -wp $realfile`

realfile=${realfile}-randpermute

halflinenum=$((linenum/2))
columns=`head -1 $transf | awk '{print NF}'`
echo columns=$columns

rm -rf /tmp/tempdatafolder/*
cat $realfile | head -n $halflinenum > /tmp/tempdatafolder/1.txt
cat $realfile | head -n $linenum | tail -n $halflinenum > /tmp/tempdatafolder/2.txt

diff -q /tmp/tempdatafolder/1.txt /tmp/tempdatafolder/2.txt
#diff -s /tmp/tempdatafolder/1.txt /tmp/tempdatafolder/2.txt

./run.sh /tmp/tempdatafolder $columns 65 $1 $2
date

set +x
