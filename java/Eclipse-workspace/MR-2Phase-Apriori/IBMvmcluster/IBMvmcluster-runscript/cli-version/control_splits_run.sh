#!/usr/bin/env bash

if [ $# -lt 3 ]; then
  echo Usage $0 /path/to/file p /path/to/outdir [noreupload]
  exit 1
fi

echo Warning, this script only works for 1000 lines now
rows=1000

outdir=$3
outdirname=`basename $outdir`
absoutdir=`readlink -f $outdir`
absoutdirpath=`dirname $absoutdir`/$outdirname

if [ ! -e $absoutdirpath ]; then
  echo $absoutdirpath does not exist
  exit 2
fi

if [ -f $absoutdirpath ]; then
  echo $outdir is not a directory
  exit 2
fi

outdirfind=`find $absoutdirpath | wc -l`
if [ $outdirfind -gt 1 ]; then
  echo $outdir is not empty
  exit 2
fi

absme=`readlink -f $0`
abshere=`dirname $absme`

file=$1
filename=`basename $file`
absfile=`readlink -f $file`
absfilepath=`dirname $absfile`

splits=$2

if [ $splits -gt 20 ]; then
  echo $splits bigger than 20
  exit 2
fi

splitrows=$((rows/splits))
remaining=$rows
loop=0

while [ $remaining -ge $splitrows ]; do
  loop=$((loop+1))
  echo $splitrows
  cat $absfile | head -n $remaining | tail -n $splitrows > $absoutdirpath/$loop.txt
  remaining=$((remaining-splitrows))
#  echo $remaining
done

if [ $remaining -gt 0 ]; then
  loop=$((loop+1))
  echo $remaining
  cat $absfile | head -n $remaining > $absoutdirpath/$loop.txt
fi

columns=`head -1 $absoutdirpath/1.txt | awk '{print NF}'`
echo columns=$columns

./run.sh $absoutdirpath $columns 51 $4
echo splits=$splits
echo columns=$columns
date
rm -rf $absoutdirpath/*
