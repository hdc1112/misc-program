#!/usr/bin/env bash

absme=`readlink -f $0`
abshere=`dirname $absme`

cd $abshere/../hadoop-2.2.0/
./bin/hdfs dfs -rm -r -f hdfs://hadoop1:9000/output-test-1stphase
./bin/hdfs dfs -rm -r -f hdfs://hadoop1:9000/output-test
./bin/hadoop jar /tmp/mr-2phase-apriori.jar hdfs://hadoop1:9000/input-test hdfs://hadoop1:9000/output-test 7 50
