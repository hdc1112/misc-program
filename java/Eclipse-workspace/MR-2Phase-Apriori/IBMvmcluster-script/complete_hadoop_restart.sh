#!/usr/bin/env bash

set -x

absme=`readlink -f $0`
abshere=`dirname $absme`

cd $abshere

ssh -n dachuan@ibmvm1 ./hadoop-2.2.0/sbin/stop-yarn.sh
ssh -n dachuan@ibmvm1 ./hadoop-2.2.0/sbin/stop-dfs.sh
ssh -n dachuan@ibmvm1 killall -9 java
ssh -n dachuan@ibmvm2 killall -9 java
ssh -n dachuan@ibmvm3 killall -9 java

for node in dachuan@ibmvm1 dachuan@ibmvm2 dachuan@ibmvm3; do 
  echo $node
  ssh -n $node rm -rf hadoop-2.2.0/logs/* dfs/name/* dfs/data/* temp/*
done

ssh -n dachuan@ibmvm1 ./hadoop-2.2.0/bin/hadoop namenode -format
ssh -n dachuan@ibmvm1 ./hadoop-2.2.0/sbin/start-yarn.sh
ssh -n dachuan@ibmvm1 ./hadoop-2.2.0/sbin/start-dfs.sh
echo dachuan@ibmvm1 && ssh -n dachuan@ibmvm1 jps
echo dachuan@ibmvm2 && ssh -n dachuan@ibmvm2 jps
echo dachuan@ibmvm3 && ssh -n dachuan@ibmvm3 jps

set +x
