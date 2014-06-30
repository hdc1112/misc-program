#!/usr/bin/env bash

# default value stage
columns=
minsupport=
tolerate=
enableopt1=
enableopt2=
worknode=ibmvm1
user=dachuan
inpath=hdfs://$worknode:9000/input-test 
outpath=hdfs://$worknode:9000/output-test 

# definition, parsing, interrogation stages
while getopts ":i:o:c:m:t:w:u:pq" o; do
  case $o in
    i)
      inpath=$OPTARG
      ;;
    o)
      outpath=$OPTARG
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
    p)
      enableopt1="--enableOPT1"
      ;;
    q)
      enableopt2="--enableOPT2"
      ;;
    *)
      echo invalid argument >&2
      exit 1
      ;;
  esac
done

# arguments show stage
echo `basename $0` arguments list
echo inputpath=$inpath
echo outputpath=$outpath
echo columns=$columns
echo minsupport=$minsupport
echo tolerate=$tolerate
echo enableopt1=$enableopt1
echo enableopt2=$enableopt2
echo worknode=$worknode
echo user=$user

# verify arguments stage, exit if necessary (skip)

# standard header
absme=`readlink -f $0`
abshere=`dirname $absme`

# argument path absolutify

# enter my work directory
cd $abshere

# main logic
set -x

echo "/home/$user/hadoop-2.2.0/bin/hadoop jar /tmp/mr-2phase-apriori.jar --inpath $inpath --outpath $outpath --columns $columns --minsupport $minsupport --tolerate $tolerate $enableopt1 $enableopt2" > /tmp/remote-exe.sh
scp /tmp/remote-exe.sh $user@$worknode:/tmp/
ssh -n $user@$worknode chmod +x /tmp/remote-exe.sh
ssh -n $user@$worknode /tmp/remote-exe.sh

set +x
