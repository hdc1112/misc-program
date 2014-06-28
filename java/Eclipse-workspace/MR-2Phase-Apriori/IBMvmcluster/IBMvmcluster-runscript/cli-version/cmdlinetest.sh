#!/bin/bash

# cmd arguments stages
# 1st, default value stage
# 2nd, defining options stage
# 3rd, parsing stage
# 4th, interrogation stage
# 5th, arguments show stage
# 6th, arguments verify stage

usage() { echo "Usage: $0 [-s <45|90>] [-p <string>]" 1>&2; exit 1; }

while getopts ":s:p:" o; do
  case "${o}" in
    s)
      s=${OPTARG}
      ((s == 45 || s == 90)) || usage
      ;;
    p)
      p=${OPTARG}
      ;;
    *)
      usage
      ;;
  esac
done
shift $((OPTIND-1))

if [ -z "${s}" ] || [ -z "${p}" ]; then
  usage
fi

echo "s = ${s}"
echo "p = ${p}"
echo $@
