#!/usr/bin/env bash

if [ $# -lt 2 ]; then
  echo Usage $0 /path/to/file columns [noreupload]
  exit 1
fi

echo Warning, this script only works for 1000 lines now

absme=`readlink -f $0`
abshere=`dirname $absme`

file=$1
filename=`basename $file`
absfile=`readlink -f $file`
absfilepath=`dirname $absfile`

cd $abshere

javac RandomPermuteRows.java
java RandomPermuteRows `cygpath -wp $absfile` $2
head -n 500 $absfile > $absfilepath/${filename}-1
head -n 1000 $absfile | tail -n 500 > $absfilepath/${filename}-2

rm -rf /tmp/tempdatadir
mkdir /tmp/tempdatadir
rm -f $absfile
mv $absfilepath/${filename}-1 /tmp/tempdatadir
mv $absfilepath/${filename}-2 /tmp/tempdatadir

./run.sh /tmp/tempdatadir $2 51 $3
date
