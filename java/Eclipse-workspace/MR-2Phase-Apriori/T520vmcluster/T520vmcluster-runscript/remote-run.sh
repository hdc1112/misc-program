#!/usr/bin/env bash

absme=`readlink -f $0`
abshere=`dirname $absme`

cd $abshere

echo "/home/dachuan/hadoop-2.2.0/bin/hadoop jar /tmp/mr-2phase-apriori.jar hdfs://hadoop1:9000/input-test hdfs://hadoop1:9000/output-test $1 $2" > /tmp/remote-exe.sh
scp /tmp/remote-exe.sh hadoop1:/tmp/
ssh -n hadoop1 chmod +x /tmp/remote-exe.sh
ssh -n hadoop1 /tmp/remote-exe.sh
