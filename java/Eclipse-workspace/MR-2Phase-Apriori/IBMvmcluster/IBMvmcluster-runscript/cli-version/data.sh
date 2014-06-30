#!/usr/bin/env bash

# default value stage
folder=
noreupload=

# definition, parsing, interrogation stages
while getopts ":f:n" o; do
  case $o in
    f) 
      folder=$OPTARG
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

ssh -n dachuan@ibmvm1 ./hadoop-2.2.0/bin/hdfs dfs -rm -r -f /output-test-1stphase /output-test

if [ "$noreupload" = "noreupload" ]; then
  echo Skip data reupload, use previously uploaded input
else
  ssh -n dachuan@ibmvm1 rm -rf /tmp/$foldername
  echo start uploading data to hdfs && date
  scp -r $folder dachuan@ibmvm1:/tmp/
  echo data uploaded to hdfs && date
  ssh -n dachuan@ibmvm1 ./hadoop-2.2.0/bin/hdfs dfs -rm -r -f /input-test

  ssh -n dachuan@ibmvm1 ./hadoop-2.2.0/bin/hdfs dfs -mkdir /input-test
  ssh -n dachuan@ibmvm1 ./hadoop-2.2.0/bin/hdfs dfs -copyFromLocal /tmp/$foldername/* /input-test/

fi

set +x
