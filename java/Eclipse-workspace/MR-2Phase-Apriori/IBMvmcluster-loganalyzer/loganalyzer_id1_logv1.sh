#!/usr/bin/env bash

# lesson for future
# inside function, use "local" keyword to declare variable

# standard header
set -x
absme=`readlink -f $0`
abshere=`dirname $absme`
me=`basename $absme`
cd $abshere

# output, and output format of this script is in 
# 6.19.2014.design.exp.chess.dat.header.file.xlsx

# interactive input
# TODO read the backup log folder's name from user
# TODO read the stub program's log path from user
# TODO read the output file path from user
#read -p "Server backup log folder path: " user_servbakupf
read -p "Statistics output path: " user_statpath
if [ -z $user_statpath ]; then
  echo Invalid Statistics output path
  exit 2
fi
rm -f $user_statpath
touch $user_statpath

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
# log version
logv="logv 1"
prefix="\[MR2PhaseApriori\]\[$logv\]\ "
# this script assume that the remote logs are organized in:
# logs-hadoop1 --> userlogs --> application_*_0001 --> container_*_0001 --> stderr,stdout
# this script assume that every log folder has exactly the same structure
# and no corrupted message
# this script has many implicit assumptions, such as #mappers are 2,
# #reducers are 1, no speculative, etc.
# Just remember, whenever there is some change, no matter what change,
# the first thought should be whether this script would still work.

# debug param
deb_clean=yes

# under this line, there's no hard-coded thing

# functions
function get_m1_1_start {
  l_totalcont=$1 && shift
  l_interestfiles=($@)
  # TODO change all the following default value to "NULL", or change in the other place, use "if empty then NULL" syntax
  m1_1_start=""
  for i in `seq 0 $((l_totalcont-1))`; do
    #echo ${l_interestfiles[$i]}
    set +x
    while read line; do
      [[ $line =~ $prefix\(1/2\)\ 0\ Map\ [tT]ask\ start\ time:\ ([0-9]*) ]]\
        && m1_1_start=${BASH_REMATCH[1]} && break
    done < <(cat ${l_interestfiles[$i]}/stderr)
    set -x
    if [ ! -z $m1_1_start ]; then
      break
    fi
  done
  echo $m1_1_start
}

function get_m1_1_end {
  l_totalcont=$1 && shift
  l_interestfiles=($@)
  m1_1_end=""
  for i in `seq 0 $((l_totalcont-1))`; do
    #echo ${l_interestfiles[$i]}
    set +x
    while read line; do
      [[ $line =~ $prefix\(1/2\)\ 0\ Map\ [tT]ask\ end\ time:\ ([0-9]*) ]]\
        && m1_1_end=${BASH_REMATCH[1]} && break
    done < <(cat ${l_interestfiles[$i]}/stderr)
    set -x
    if [ ! -z $m1_1_end ]; then
      break
    fi
  done
  echo $m1_1_end
}

function get_m1_1_loop {
  l_totalcont=$1 && shift
  l_interestfiles=($@)
  m1_1_loop=""
  for i in `seq 0 $((l_totalcont-1))`; do
    #echo ${l_interestfiles[$i]}
    set +x
    while read line; do
      [[ $line =~ $prefix\(1/2\)\ 0\ Map\ [tT]ask\ end\ time:\ ([0-9]*) ]]\
        && m1_1_loop=${BASH_REMATCH[1]} && break
    done < <(cat ${l_interestfiles[$i]}/stderr)
    set -x
    if [ ! -z $m1_1_loop ]; then
      break
    fi
  done
  echo $m1_1_loop
}

function get_m1_1 {
  l_totalcont=$1 && shift
  l_interestfiles=($@)
  m1_1=""
  for i in `seq 0 $((l_totalcont-1))`; do
    #echo ${l_interestfiles[$i]}
    set +x
    while read line; do
      [[ $line =~ $prefix\(1/2\)\ 0\ Map\ [tT]ask\ execution\ time:\ ([0-9]*) ]]\
        && m1_1=${BASH_REMATCH[1]} && break
    done < <(cat ${l_interestfiles[$i]}/stderr)
    set -x
    if [ ! -z $m1_1 ]; then
      break
    fi
  done
  echo $m1_1
}

function get_m1_2_start {
  l_totalcont=$1 && shift
  l_interestfiles=($@)
  m1_2_start=""
  for i in `seq 0 $((l_totalcont-1))`; do
    #echo ${l_interestfiles[$i]}
    set +x
    while read line; do
      [[ $line =~ $prefix\(1/2\)\ 1\ Map\ [tT]ask\ start\ time:\ ([0-9]*) ]]\
        && m1_2_start=${BASH_REMATCH[1]} && break
    done < <(cat ${l_interestfiles[$i]}/stderr)
    set -x
    if [ ! -z $m1_2_start ]; then
      break
    fi
  done
  echo $m1_2_start
}

function get_m1_2_end {
  l_totalcont=$1 && shift
  l_interestfiles=($@)
  m1_2_end=""
  for i in `seq 0 $((l_totalcont-1))`; do
    #echo ${l_interestfiles[$i]}
    set +x
    while read line; do
      [[ $line =~ $prefix\(1/2\)\ 1\ Map\ [tT]ask\ end\ time:\ ([0-9]*) ]]\
        && m1_2_end=${BASH_REMATCH[1]} && break
    done < <(cat ${l_interestfiles[$i]}/stderr)
    set -x
    if [ ! -z $m1_2_end ]; then
      break
    fi
  done
  echo $m1_2_end
}

function get_m1_2 {
  l_totalcont=$1 && shift
  l_interestfiles=($@)
  m1_2=""
  for i in `seq 0 $((l_totalcont-1))`; do
    #echo ${l_interestfiles[$i]}
    set +x
    while read line; do
      [[ $line =~ $prefix\(1/2\)\ 1\ Map\ [tT]ask\ execution\ time:\ ([0-9]*) ]]\
        && m1_2=${BASH_REMATCH[1]} && break
    done < <(cat ${l_interestfiles[$i]}/stderr)
    set -x
    if [ ! -z $m1_2 ]; then
      break
    fi
  done
  echo $m1_2
}

function get_r1_1_start {
  l_totalcont=$1 && shift
  l_interestfiles=($@)
  r1_1_start=""
  for i in `seq 0 $((l_totalcont-1))`; do
    #echo ${l_interestfiles[$i]}
    set +x
    while read line; do
      [[ $line =~ $prefix\(1/2\)\ 0\ Reduce\ [tT]ask\ start\ time:\ ([0-9]*) ]]\
        && r1_1_start=${BASH_REMATCH[1]} && break
    done < <(cat ${l_interestfiles[$i]}/stderr)
    set -x
    if [ ! -z $r1_1_start ]; then
      break
    fi
  done
  echo $r1_1_start
}

function get_r1_1_end {
  l_totalcont=$1 && shift
  l_interestfiles=($@)
  r1_1_end=""
  for i in `seq 0 $((l_totalcont-1))`; do
    #echo ${l_interestfiles[$i]}
    set +x
    while read line; do
      [[ $line =~ $prefix\(1/2\)\ 0\ Reduce\ [tT]ask\ end\ time:\ ([0-9]*) ]]\
        && r1_1_end=${BASH_REMATCH[1]} && break
    done < <(cat ${l_interestfiles[$i]}/stderr)
    set -x
    if [ ! -z $r1_1_end ]; then
      break
    fi
  done
  echo $r1_1_end
}

function get_r1_1 {
  l_totalcont=$1 && shift
  l_interestfiles=($@)
  r1_1=""
  for i in `seq 0 $((l_totalcont-1))`; do
    #echo ${l_interestfiles[$i]}
    set +x
    while read line; do
      [[ $line =~ $prefix\(1/2\)\ 0\ Reduce\ execution\ time:\ ([0-9]*) ]]\
        && r1_1=${BASH_REMATCH[1]} && break
    done < <(cat ${l_interestfiles[$i]}/stderr)
    set -x
    if [ ! -z $r1_1]; then
      break
    fi
  done
  echo $r1_1
}

function clean {
  l_cleanf=$1
  if [ $l_cleanf = "yes" ]; then
    if [ ! -z $in_localrepo ]; then
      rm -r -f $in_localrepo
    fi
  fi
}

# prepare for main logic
for n in $in_nodes; do
  scp -r -q $in_user@$n:$in_remotehadooplogs $in_localrepo/logs-$n
done
# TODO try to back up the log folder into hadoop-logs in server node

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

if [ $mid_maxjobid -lt 0 ]; then
  echo Exit, sample space too small
  clean $deb_clean
  exit 1
fi

echo mid_maxjobid=$mid_maxjobid
mid_totalrun=$((mid_maxjobid/2))
echo mid_totalrun=$mid_totalrun

for run in `seq 1 $mid_totalrun`; do
  # for phase 1
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

  # now i have phase 1's each task's container stderr
  #for j in `seq 1 $totalcont`; do
  #echo ${interestfiles[$j]}
  #done

  m1_1_start=$(get_m1_1_start $totalcont "${interestfiles[@]}")
  echo -n $m1_1_start" " >> $user_statpath

  m1_1_end=$(get_m1_1_end $totalcont "${interestfiles[@]}")
  echo -n $m1_1_end" " >> $user_statpath

  # TODO in program, add map task id into log, and update log version
  #m1_1_loop=$(get_m1_1_loop $totalcont "${interestfiles[@]}")
  #echo -n $m1_1_loop" "

  m1_1=$(get_m1_1 $totalcont "${interestfiles[@]}")
  echo -n $m1_1" " >> $user_statpath

  m1_2_start=$(get_m1_2_start $totalcont "${interestfiles[@]}")
  echo -n $m1_2_start" " >> $user_statpath

  m1_2_end=$(get_m1_2_end $totalcont "${interestfiles[@]}")
  echo -n $m1_2_end" " >> $user_statpath

  #m1_2_loop=$(get_m1_2_loop $totalcont "${interestfiles[@]}")
  #echo -n $m1_2_loop" "

  m1_2=$(get_m1_2 $totalcont "${interestfiles[@]}")
  echo -n $m1_2" " >> $user_statpath

  r1_1_start=$(get_r1_1_start $totalcont "${interestfiles[@]}")
  echo -n $r1_1_start" " >> $user_statpath

  r1_1_end=$(get_r1_1_end $totalcont "${interestfiles[@]}")
  echo -n $r1_1_end" " >> $user_statpath

  r1_1=$(get_r1_1 $totalcont "${interestfiles[@]}")
  echo -n $r1_1" " >> $user_statpath


  # for phase 2
  # TODO keep developing these code
  # m2_1_start
  # m2_1_end
  # m2_1
  # m2_1_cache_total
  # m2_1_cache_hit
  # m2_2_start
  # m2_2_end
  # m2_2
  # m2_2_cache_total
  # m2_2_cache_hit
  # r2_1_start
  # r2_1_end
  # r2_1

  echo >> $user_statpath

done

# clean
#if [ $deb_clean == "yes" ]; then
#  rm -r -f $in_localrepo
#fi
clean $deb_clean

# standard tail
set +x
