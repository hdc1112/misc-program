#!/usr/bin/env bash

retval=""

while read line
do
  retval=`cygpath -wp $line`\;$retval
done < "${1:-/proc/${$}/fd/0}"

echo $retval
