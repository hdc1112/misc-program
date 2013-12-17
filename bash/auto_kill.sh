#!/usr/bin/env bash

# this script is designed to automatically run the example for 100
# times, and each time 3 minutes

# this script is very ad hoc, don't use it for general purpose use

cd /home/huangda/incubator-spark/

for i in `seq 31 100`; do
  ./run-example org.apache.spark.streaming.examples.StatefulNetworkWordCount local-cluster[5,1,1024] localhost 9999 > /home/huangda/Spark-FT-study-project-git/0-raw-data/0-exp-12-16-2013-fifo-1--$i.txt &
  pid=$!
  echo waiting $pid for 180s ...
  sleep 180
  kill -9 $pid
  echo take a 1 minute rest before next experiment ...
  sleep 60
done
