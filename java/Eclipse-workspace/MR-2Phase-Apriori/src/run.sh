#!/usr/bin/env bash

absme=`readlink -f $0`
abshere=`dirname $absme`

if [ $# -lt 3 ]; then
  echo Usage input-folder items minsupport
  exit
fi

dataabspath=`readlink -f $1`
cd $abshere

./jar.sh
if [ "$4" != "noreupload" ]; then
  ./data.sh $dataabspath
else
  ./data.sh $dataabspath noreupload
fi
./remote-run.sh $2 $3
