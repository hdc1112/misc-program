#!/bin/bash

if [ $# != 1 ]; then
  echo Usage: /path/to/script pdf.pdf
  exit 1
fi

STDLINUX=stdlinux.cse.ohio-state.edu
ACCOUNT=huangda
SSHSERVER=${ACCOUNT}@${STDLINUX}
TEMPFLIE=abc.pdf

echo scp \""$1"\" ${SSHSERVER}:abc.pdf
scp $1 ${SSHSERVER}:abc.pdf
ssh -n $SSHSERVER ./printer.sh abc.pdf
ssh -n $SSHSERVER rm abc.pdf
ssh -n $SSHSERVER ls abc.pdf
