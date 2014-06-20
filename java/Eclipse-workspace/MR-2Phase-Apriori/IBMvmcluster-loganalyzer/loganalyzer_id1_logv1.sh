#!/usr/bin/env bash

# standard header
set -x
absme=`readlink -f $0`
abshere=`dirname $absme`
me=`basename $absme`
cd $abshere

# output format of this script is in 
# 6.19.2014.design.exp.chess.dat.header.file.xlsx

# input of this script
# hardware configuration
# this script is only designed for mr2phaseapriori
# nodes list
in_nodes="hadoop1 hadoop2 hadoop3"
in_user="dachuan"
in_sshnodes="$in_user@hadoop1 $in_user@hadoop2 $in_user@hadoop3"
in_localrepo=/tmp/${me}.$$.localrepo
if [ -d $in_localrepo ]; then
  rm -r -f $in_localrepo
fi
mkdir $in_localrepo
in_remotehadoop=/home/$in_user/hadoop-2.2.0
in_remotehadooplogs=$in_remotehadoop/logs
in_remotehadoopbakuplogs=/home/$in_user/hadoop-logs
for n in $in_sshnodes; do
  ssh -n $n "if [ ! -d $in_remotehadoopbakuplogs ]; \
    then mkdir $in_remotehadoopbakuplogs; fi"
done
# this script assume log is in stderr in the following folder
in_userlogs=userlogs
# this script assume that the remote logs are organized in:
# logs-hadoop1 --> userlogs --> application_*_0001 --> container_*_0001 --> stderr,stdout
# this script assume that every log folder has exactly the same structure
# and no corrupted message

# debug param
deb_clean=yes

# under this line, there's no hard-coded thing

# prepare for main logic
for n in $in_nodes; do
  scp -r -q $in_user@$n:$in_remotehadooplogs $in_localrepo/logs-$n
done

# main logic
mid_maxjobid=0
set +x
for n in $in_nodes; do
  while read name; do
    [[ $name =~ application_[0-9]+_0*([1-9][0-9]*) ]] \
      && jobid=${BASH_REMATCH[1]}
    if [ $jobid -gt $mid_maxjobid ]; then
      mid_maxjobid=$jobid
    fi
  done < <(ls $in_localrepo/logs-$n/$in_userlogs) 
done
set -x
echo mid_maxjobid=$mid_maxjobid

# we omit the last mr2phaseapriori run
# since we don't know whether it's still running
if [ $((mid_maxjobid%2)) -eq 0 ]; then
  mid_maxjobid=$((mid_maxjobid-2))
else
  mid_maxjobid=$((mid_maxjobid-1))
fi

echo mid_maxjobid=$mid_maxjobid
mid_totalrun=$((mid_maxjobid/2))
echo mid_totalrun=$mid_totalrun

for run in `seq 1 $mid_totalrun`; do
  runid=$((run*2-1))
  run1stfolder=

  totalcont=0
  interestfiles=
  for n in $in_nodes; do
    set +x
    while read name; do
      [[ $name =~ (application_[0-9]+_0*$runid) ]] \
        && run1stfolder=${BASH_REMATCH[1]} && break
    done < <(ls $in_localrepo/logs-$n/$in_userlogs) 
    set -x
    run1stfolder=$in_localrepo/logs-$n/$in_userlogs/$run1stfolder

    # stands for total container
    set +x
    while read container; do
      totalcont=$((totalcont+1))
      containerstderr=$run1stfolder/$container
      interestfiles[$totalcont]=$containerstderr
    done < <(ls $run1stfolder)
    set -x

  done

  for j in `seq 1 $totalcont`; do
    echo haha ${interestfiles[$j]}
  done

done


# clean
if [ $deb_clean == "yes" ]; then
  rm -r -f $in_localrepo
fi

# standard tail
set +x
