#!/bin/bash

default_template=30
default_line_words=80

if [ $# != 2 ]; then
  echo Usage `basename $0` 300 /tmp/abc.file
  echo Unit is MB
  echo By default, the template size is 30MB
  echo By default, every line has 80 words
  exit 1
fi

round=500000

rm -f $2
touch $2

# first stage, get a template file
first_file=$2.1st.tmp
rm -f $first_file
touch $first_file
while true; do
  filesize_bytes=$(stat -c%s $first_file)
  filesize_mb=$((filesize_bytes / 1024 / 1024))

  if [ $filesize_mb -ge $default_template ]; then
    echo First stage done.
    du -sh $first_file
    break
  fi

  line_ws=0
  for i in `seq 1 $round`; do
    ((line_ws++))
    echo -n $RANDOM >> $first_file
    if [ $line_ws -ge $default_line_words ]; then
      echo >> $first_file
      line_ws=0
    else
      echo -n " " >> $first_file
    fi
  done
done

# second stage, have multiple copies
while true; do
  filesize_bytes=$(stat -c%s $2)
  filesize_mb=$((filesize_bytes / 1024 / 1024))

  if [ $filesize_mb -ge $1 ]; then
    echo Second stage done
    du -sh $2
    break
  fi

  cat $first_file >> $2
done

# clean up
rm -f $first_file
