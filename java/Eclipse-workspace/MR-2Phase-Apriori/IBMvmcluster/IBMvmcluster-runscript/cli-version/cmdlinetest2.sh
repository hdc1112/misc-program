#!/usr/bin/env bash

# definition, parsing, interrogation stage
while getopts ":im:t:" opt; do
  case $opt in
    i)
      #items=$OPTARG
      ;;
    m)
      minsupport=$OPTARG
      ;;
    t)
      tolerate=$OPTARG
      ;;
    *)
      echo "wrong"
      exit
      ;;
  esac
done

# cmd line opts show stage
echo $items
echo $minsupport
echo $tolerate
