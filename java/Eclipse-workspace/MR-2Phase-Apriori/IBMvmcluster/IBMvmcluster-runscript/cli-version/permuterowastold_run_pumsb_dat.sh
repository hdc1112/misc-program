#!/usr/bin/env bash

# default value stage
permutefile=
tolerate=
noreupload=
minsupport=
enableopt1=
enableopt2=

# definition, parsing, interrogation stages
while getopts ":f:t:m:pqn" o; do
  case $o in 
    f)
      permutefile=$OPTARG
      ;;
    t)
      tolerate=$OPTARG
      ;;
    m)
      minsupport=$OPTARG
      ;;
    p)
      enableopt1="-p"
      ;;
    q)
      enableopt2="-q"
      ;;
    n)
      noreupload="-n"
      ;;
    *)
      echo invalid argument >&2
      exit 1
      ;;
  esac
done

# arguments show stage
echo `basename $0` arguments list
echo permutefile=$permutefile
echo tolerate=$tolerate
echo noreupload=$noreupload
echo minsupport=$minsupport
echo enableopt1=$enableopt1
echo enableopt2=$enableopt2

# verify arguments stage (skip)

# standard header
absme=`readlink -f $0`
abshere=`dirname $absme`

# arg path absolutify

# enter wkdir
cd $abshere

# main logic
set -x
storedir=/tmp
filename=pumsb.dat
transf=$storedir/$filename.transf

if [ ! -f $transf ]; then
  echo did not find $transf, transform now
  ./transform_pumsb.sh
elif [ `cat $storedir/$filename | wc -l` != `cat $transf | wc -l` ]; then
  echo found $transf, but it is corrupted
  ./transform_pumsb.sh
else
  echo found $transf, it looks good
fi

# now we have pumsb.dat

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

./run.sh -d /tmp/tempdatafolder -c $columns -m $minsupport -t $tolerate $enableopt1 $enableopt2 $noreupload
date

set +x
