#!/usr/bin/env bash

# default value stage
dataabspath=
columns=
minsupport=
tolerate=
enableopt1=
enableopt2=
noreupload=
worknode=ibmvm1
user=dachuan
phase1minsup=
phase1minsupbeta=
solution1=  #j
solution1param1=  #k

# definition, parsing, interrogation stages
while getopts ":d:c:m:t:w:u:x:y:k:pqnj" o; do
  case $o in
    d)
      dataabspath=$OPTARG
      ;;
    c)
      columns=$OPTARG
      ;;
    m)
      minsupport=$OPTARG
      ;;
    t)
      tolerate=$OPTARG
      ;;
    w)
      worknode=$OPTARG
      ;;
    u)
      user=$OPTARG
      ;;
    x)
      phase1minsup="-x $OPTARG"
      ;;
    y)
      phase1minsupbeta="-y $OPTARG"
      ;;
    k)
      solution1param1="-k $OPTARG"
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
    j)
      solution1="-j"
      ;;
    *)
      echo invalid argument >&2
      exit 1
      ;;
  esac
done

# arguments show stage
echo `basename $0` arguments list
echo dataabspath=$dataabspath
echo columns=$columns
echo minsupport=$minsupport
echo tolerate=$tolerate
echo enableopt1=$enableopt1
echo enableopt2=$enableopt2
echo noreupload=$noreupload
echo worknode=$worknode
echo user=$user
echo phase1minsup=$phase1minsup
echo phase1minsupbeta=$phase1minsupbeta
echo solution1=$solution1
echo solution1param1=$solution1param1

# verify arguments stage (skip)

# standard header
absme=`readlink -f $0`
abshere=`dirname $absme`

# argument path absolutify
dataabspath=`readlink -f $dataabspath`

# enter my work directory
cd $abshere

# main logic
set -x

./jar.sh -w $worknode -u $user
./data.sh -f $dataabspath $noreupload -w $worknode -u $user
#./remote-run.sh -c $columns -m $minsupport -t $tolerate $enableopt1 $enableopt2 -w $worknode -u $user
./remote-run.sh -m $minsupport $enableopt1 $enableopt2 -w $worknode -u $user $phase1minsup $phase1minsupbeta $solution1 $solution1param1

set +x
