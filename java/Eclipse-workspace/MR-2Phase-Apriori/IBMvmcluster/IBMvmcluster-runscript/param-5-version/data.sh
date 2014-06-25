#!/usr/bin/env bash

set -x

absme=`readlink -f $0`
abshere=`dirname $absme`

cd $abshere

folder=`basename $1`

ssh -n dachuan@ibmvm1 ./hadoop-2.2.0/bin/hdfs dfs -rm -r -f /output-test-1stphase /output-test

if [ "$2" = "noreupload" ]; then
  echo Skip data reupload, use previously uploaded input
else
  ssh -n dachuan@ibmvm1 rm -rf /tmp/$folder
  scp -r $1 dachuan@ibmvm1:/tmp/
  ssh -n dachuan@ibmvm1 ./hadoop-2.2.0/bin/hdfs dfs -rm -r -f /input-test

  ssh -n dachuan@ibmvm1 ./hadoop-2.2.0/bin/hdfs dfs -mkdir /input-test
  ssh -n dachuan@ibmvm1 ./hadoop-2.2.0/bin/hdfs dfs -copyFromLocal /tmp/$folder/* /input-test/

fi

set +x
