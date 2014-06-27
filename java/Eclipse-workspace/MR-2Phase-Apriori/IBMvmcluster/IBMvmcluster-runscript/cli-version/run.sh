#!/usr/bin/env bash

set -x

absme=`readlink -f $0`
abshere=`dirname $absme`

if [ $# -lt 4 ]; then
  echo Usage input-folder items minsupport tolerate [noreupload]
  exit
fi

dataabspath=`readlink -f $1`
cd $abshere

./jar.sh
if [ "$5" != "noreupload" ]; then
  ./data.sh $dataabspath
else
  ./data.sh $dataabspath noreupload
fi
./remote-run.sh $2 $3 $4

set +x
