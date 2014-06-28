#!/usr/bin/env bash

# default value stage
dataabspath=
columns=
minsupport=
tolerate=
enableopt1=
enableopt2=
noreupload=

# definition, parsing, interrogation stages
while getopts ":d:c:m:t:pqn" o; do
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
echo dataabspath=$dataabspath
echo columns=$columns
echo minsupport=$minsupport
echo tolerate=$tolerate
echo enableopt1=$enableopt1
echo enableopt2=$enableopt2
echo noreupload=$noreupload

# verify arguments stage (skip)

# standard header
absme=`readlink -f $0`
abshere=`dirname $absme`

# argument path absolutify
dataabspath=`readlink -f $dataabspath`

# enter my work directory
cd $abshere

set -x

./jar.sh
./data.sh -f $dataabspath $noreupload
./remote-run.sh -c $columns -m $minsupport -t $tolerate $enableopt1 $enableopt2

set +x
