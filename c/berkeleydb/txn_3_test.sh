#!/bin/bash

DEV=/dev/sdc

echo "Run iostat -m 1 now"
read
sudo umount $DEV
sudo /sbin/mkfs.ext3 $DEV
sudo mount -t ext3 $DEV /mnt/
sudo chmod -R 777 /mnt
mkdir /mnt/mai-test-1/
echo "Start to write to database"
read
./txn_3_write.x 2>&1 | tee /tmp/txn_3_write.log
