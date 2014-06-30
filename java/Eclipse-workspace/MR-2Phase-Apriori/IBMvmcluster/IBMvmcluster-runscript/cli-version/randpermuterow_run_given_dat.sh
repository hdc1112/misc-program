#!/usr/bin/env bash

# default value stage
tolerate=
noreupload=
minsupport=
enableopt1=
enableopt2=
datname=
permutefile=    #o
worknode=ibmvm1
user=dachuan

# definition, parsing, interrogation stages
while getopts ":d:t:m:o:w:u:npq" o; do
  case $o in
    d)
      datname=$OPTARG
      ;;
    t)
      tolerate=$OPTARG
      ;;
    m)
      minsupport=$OPTARG
      ;;
    o)
      permutefile=$OPTARG
      ;;
    w)
      worknode=$OPTARG
      ;;
    u)
      user=$OPTARG
      ;;
    n)
      noreupload="-n"
      ;;
    p)
      enableopt1="-p"
      ;;
    q)
      enableopt2="-q"
      ;;
    *)
      echo invalid argument >&2
      exit 1
      ;;
  esac
done

# arguments show stage
echo `basename $0` arguments list
echo datname=$datname
echo tolerate=$tolerate
echo noreupload=$noreupload
echo minsupport=$minsupport
echo enableopt1=$enableopt1
echo enableopt2=$enableopt2
echo permutefile=$permutefile
echo worknode=$worknode
echo user=$user

# verify arguments stage (skip)

# standard header
absme=`readlink -f $0`
abshere=`dirname $absme`

# arg path absolutify

# entery wkdir
cd $abshere

# main logic
set -x

platform=`uname -o`

storedir=/tmp
filename=$datname.dat
transf=$storedir/$filename.transf

if [ ! -f $transf ]; then
  echo did not find $transf, transform now
  ./transform_given_dat.sh -d $datname
elif [ `cat $storedir/$filename | wc -l` != `cat $transf | wc -l` ]; then
  echo found $transf, but it is corrupted
  ./transform_given_dat.sh -d $datname
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

cd $abshere/../../../src
if [ ! -f hadoopclasspath.txt ]; then
  find $HOME/hadoop-2.2.0/share/hadoop -type f -name "*.jar" | $abshere/concatenate.sh > hadoopclasspath.txt
fi
classes=`cat hadoopclasspath.txt`
javac -classpath $classes PermuteRows.java
if [ -z $permutefile ]; then
  if [ $platform = "Cygwin" ]; then
    java -classpath $classes PermuteRows --datafile `cygpath -wp $realfile`
  else
    java -classpath $classes PermuteRows --datafile $realfile
  fi
else
  rm -f $permutefile
  if [ $platform = "Cygwin" ]; then
    java -classpath $classes PermuteRows --datafile `cygpath -wp $realfile` --permutefile `cygpath -wp $permutefile`
  else
    java -classpath $classes PermuteRows --datafile $realfile --permutefile $permutefile
  fi
fi
cd $abshere

realfile=${realfile}-randpermute

halflinenum=$((linenum/2))
columns=`head -1 $transf | awk '{print NF}'`
echo columns=$columns

datapath=/tmp/tempdatafolder/
rm -rf $datapath
if [ ! -d $datapath ]; then
  mkdir $datapath
fi
cat $realfile | head -n $halflinenum > $datapath/1.txt
cat $realfile | head -n $linenum | tail -n $halflinenum > $datapath/2.txt

diff -q $datapath/1.txt $datapath/2.txt

./run.sh -d $datapath -c $columns -m $minsupport -t $tolerate $enableopt1 $enableopt2 $noreupload -w $worknode -u $user
date

set +x
