
clean:
	find -type f -name "*~" | xargs rm -rf
	find -type f -name "*stackdump" | xargs rm -rf
