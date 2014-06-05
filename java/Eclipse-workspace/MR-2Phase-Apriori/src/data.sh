#!/usr/bin/env bash

absme=`readlink -f $0`
abshere=`dirname $absme`

cd $abshere

folder=`basename $1`

ssh -n hadoop1 rm -rf /tmp/$folder
scp -r $1 hadoop1:/tmp/
ssh -n hadoop1 ./hadoop-2.2.0/bin/hdfs dfs -rm -r -f /input-test
ssh -n hadoop1 ./hadoop-2.2.0/bin/hdfs dfs -rm -r -f /output-test-1stphase
ssh -n hadoop1 ./hadoop-2.2.0/bin/hdfs dfs -rm -r -f /output-test

ssh -n hadoop1 ./hadoop-2.2.0/bin/hdfs dfs -mkdir /input-test
ssh -n hadoop1 ./hadoop-2.2.0/bin/hdfs dfs -copyFromLocal /tmp/$folder/* /input-test/
