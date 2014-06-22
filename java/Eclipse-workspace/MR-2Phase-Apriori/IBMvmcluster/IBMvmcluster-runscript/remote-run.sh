#!/usr/bin/env bash

absme=`readlink -f $0`
abshere=`dirname $absme`

cd $abshere

echo "/home/dachuan/hadoop-2.2.0/bin/hadoop jar /tmp/mr-2phase-apriori.jar hdfs://ibmvm1:9000/input-test hdfs://ibmvm1:9000/output-test $1 $2" > /tmp/remote-exe.sh
scp /tmp/remote-exe.sh dachuan@ibmvm1:/tmp/
ssh -n dachuan@ibmvm1 chmod +x /tmp/remote-exe.sh
ssh -n dachuan@ibmvm1 /tmp/remote-exe.sh
