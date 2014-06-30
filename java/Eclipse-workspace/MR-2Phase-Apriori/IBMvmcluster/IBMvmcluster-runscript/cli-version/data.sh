#!/usr/bin/env bash

# default value stage
folder=
noreupload=
worknode=ibmvm1
user=dachuan

# definition, parsing, interrogation stages
while getopts ":f:w:u:n" o; do
  case $o in
    f) 
      folder=$OPTARG
      ;;
    w)
      worknode=$OPTARG
      ;;
    u)
      user=$OPTARG
      ;;
    n)
      noreupload=noreupload
      ;;
    *)
      echo invalid argument >&2
      exit 1
      ;;
  esac
done

# arguments show stage
echo `basename $0` arguments list
echo folder=$folder
echo noreupload=$noreupload
echo worknode=$worknode
echo user=$user

#verify arguments stage (skip)

# standard header
absme=`readlink -f $0`
abshere=`dirname $absme`

# argument path absolutify
folder=`readlink -f $folder`
foldername=`basename $folder`

# enter my work directory
cd $abshere

# main logic
set -x

ssh -n $user@$worknode ./hadoop-2.2.0/bin/hdfs dfs -rm -r -f /output-test-1stphase /output-test

if [ "$noreupload" = "noreupload" ]; then
  echo Skip data reupload, use previously uploaded input
else
  ssh -n $user@$worknode rm -rf /tmp/$foldername
  echo start uploading data to hdfs && date
  scp -r $folder $user@$worknode:/tmp/
  echo data uploaded to hdfs && date
  ssh -n $user@$worknode ./hadoop-2.2.0/bin/hdfs dfs -rm -r -f /input-test

  ssh -n $user@$worknode ./hadoop-2.2.0/bin/hdfs dfs -mkdir /input-test
  ssh -n $user@$worknode ./hadoop-2.2.0/bin/hdfs dfs -copyFromLocal /tmp/$foldername/* /input-test/

fi

set +x
