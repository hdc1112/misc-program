#!/usr/bin/env bash

absme=`readlink -f $0`
abshere=`dirname $absme`

cd $abshere

ssh -n hadoop1 ./hadoop-2.2.0/sbin/stop-yarn.sh
ssh -n hadoop1 ./hadoop-2.2.0/sbin/stop-dfs.sh
ssh -n hadoop1 killall -9 java
ssh -n hadoop2 killall -9 java
ssh -n hadoop3 killall -9 java

for node in hadoop1 hadoop2 hadoop3; do 
  echo $node
  ssh -n $node rm -rf hadoop-2.2.0/logs/* dfs/name/* dfs/data/* temp/*
done

ssh -n hadoop1 ./hadoop-2.2.0/bin/hadoop namenode -format
ssh -n hadoop1 ./hadoop-2.2.0/sbin/start-yarn.sh
ssh -n hadoop1 ./hadoop-2.2.0/sbin/start-dfs.sh
echo hadoop1 && ssh -n hadoop1 jps
echo hadoop2 && ssh -n hadoop2 jps
echo hadoop3 && ssh -n hadoop3 jps
