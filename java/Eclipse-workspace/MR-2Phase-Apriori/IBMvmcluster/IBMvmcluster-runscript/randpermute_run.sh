#!/usr/bin/env bash

if [ $# != 1 ]; then
  echo Usage $0 /path/to/file
  exit 1
fi

echo Warning, this script only works for 1000 lines now

absme=`readlink -f $0`
abshere=`dirname $absme`

datafilepath=`readlink -f $1`

rm -f /tmp/abc.txt
cat $datafilepath/* > /tmp/abc.txt
rm -f $datafilepath/*
mv /tmp/abc.txt $datafilepath/data.txt

datafile=`readlink -f $datafilepath/data.txt`
datafilepath=`dirname $datafile`
datafilename=`basename $datafile`

cd $abshere

javac PermuteRows.java
java PermuteRows `cygpath -wp $datafile`
head -n 500 ${datafile}-randpermute > $datafilepath/${datafilename}-1
head -n 1000 ${datafile}-randpermute | tail -n 500 > $datafilepath/${datafilename}-2

rm -rf /tmp/tempdatadir
mkdir /tmp/tempdatadir
rm -f ${datafile}-randpermute
mv $datafilepath/${datafilename}-1 /tmp/tempdatadir
mv $datafilepath/${datafilename}-2 /tmp/tempdatadir

./run.sh /tmp/tempdatadir 15 49
