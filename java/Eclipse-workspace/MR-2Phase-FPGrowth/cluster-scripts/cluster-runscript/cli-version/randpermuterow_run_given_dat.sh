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
skip= #skip permuting rows
phase1minsup= #x
phase1minsupbeta= #y
solution1=  #j
solution1param1=  #k

# definition, parsing, interrogation stages
while getopts ":d:t:m:o:w:u:x:y:k:snpqj" o; do
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
    s)
      skip=yes
      ;;
    x)
      phase1minsup="-x $OPTARG"
      ;;
    y)
      phase1minsupbeta="-y $OPTARG"
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
    j)
      solution1="-j"
      ;;
    k)
      solution1param1="-k $OPTARG"
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
echo skip=$skip
echo phase1minsup=$phase1minsup
echo phase1minsupbeta=$phase1minsupbeta
echo solution1=$solution1
echo solution1param1=$solution1param1

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
echo totallinenum=$totallinenum
linenum=$totallinenum
#linenum=$((linenum/2))
#linenum=$((linenum/2))
#linenum=$((linenum/2))

#realfile=$transf.realfile
realfile=$storedir/$filename.realfile
#cat $transf | head -n $linenum > $realfile
cat $storedir/$filename | head -n $linenum > $realfile

if [ -z $skip ]; then
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
fi

halflinenum=$((linenum/2))
#columns=`head -1 $transf | awk '{print NF}'`
#echo columns=$columns

datapath=/tmp/tempdatafolder/
rm -rf $datapath
if [ ! -d $datapath ]; then
  mkdir $datapath
fi
cat $realfile | head -n $halflinenum > $datapath/1.txt
cat $realfile | head -n $linenum | tail -n $halflinenum > $datapath/2.txt

diff -q $datapath/1.txt $datapath/2.txt

#./run.sh -d $datapath -c $columns -m $minsupport -t $tolerate $enableopt1 $enableopt2 $noreupload -w $worknode -u $user
./run.sh -d $datapath -m $minsupport $enableopt1 $enableopt2 $noreupload -w $worknode -u $user $phase1minsup $phase1minsupbeta $solution1 $solution1param1
date

set +x
