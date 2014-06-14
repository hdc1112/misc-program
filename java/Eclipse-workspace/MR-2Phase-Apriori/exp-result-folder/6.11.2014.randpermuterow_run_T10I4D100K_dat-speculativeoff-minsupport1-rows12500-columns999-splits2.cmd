./complete_hadoop_restart.sh && for i in `seq 1 100`; do echo $i; ./randpermuterow_run_T10I4D100K_dat.sh; done 2>&1 | tee /tmp/abc.log
