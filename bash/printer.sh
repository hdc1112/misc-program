#!/usr/bin/env bash

if [ $# != 1 ]; then
  echo Usage ./printer.sh /path/to/file.pdf
  exit 1
fi

stdlinux=stdlinux.cse.ohio-state.edu
file=abc.pdf
position=/home/5/huangda/$file
remote=~/printer.sh

scp $1 huangda@$stdlinux:$position
ssh -n huangda@$stdlinux 'chmod 400 ~/abc.pdf'
ssh -n huangda@$stdlinux '~/printer.sh ~/abc.pdf'
ssh -n huangda@$stdlinux rm -f $position
