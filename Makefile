
.PHONY: clean todo

clean:
	find -type f -name "*~" | xargs rm -rf
	find -type f -name "*stackdump" | xargs rm -rf

todo:
	find -type f -name "*.java" | xargs grep -in "TODO:" || true
	find -type f -name "*.c" | xargs grep -in "TODO:" || true
	find -type f -name "*.cpp" | xargs grep -in "TODO:" || true
	find -type f -name "*.h" | xargs grep -in "TODO:" || true
	cat TODO || true
