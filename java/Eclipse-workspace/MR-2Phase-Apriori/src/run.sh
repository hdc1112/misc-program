#!/usr/bin/env bash

absme=`readlink -f $0`
abshere=`dirname $absme`

cd $abshere

if [ $# != 3 ]; then
  echo Usage input-folder items minsupport
  exit
fi

./jar.sh
./data.sh $1
./remote-run.sh $2 $3
