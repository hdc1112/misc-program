#!/usr/bin/env bash

absme=`readlink -f $0`
abshere=`dirname $absme`

dataabspath=`readlink -f $1`
cd $abshere

if [ $# != 3 ]; then
  echo Usage input-folder items minsupport
  exit
fi

./jar.sh
./data.sh $dataabspath
./remote-run.sh $2 $3
