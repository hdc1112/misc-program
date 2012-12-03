.PHONY: clean todo lesson contrast tree find

clean:
	find -type f -name "*~" | xargs rm -rf || true
	find -type f -name "*stackdump" | xargs rm -rf || true
	find -type f -name "*.class" | xargs rm -rf || true
	find -type f -name "*.h.gch" | xargs rm -rf || true
	find -type f -name "*.exe" | xargs rm -rf || true
	find -type f -name "*.out" | xargs rm -rf || true

cyclic:
	find -type f -name "*.java" | xargs grep -in "CYCLIC:" || true
	find -type f -name "*.c" | xargs grep -in "CYCLIC:" || true
	find -type f -name "*.cpp" | xargs grep -in "CYCLIC:" || true
	find -type f -name "*.h" | xargs grep -in "CYCLIC:" || true
	cat CYCLIC || true

# (admin)
# when you write todo for the future, write 
# TODO:
todo:
	find -type f -name "*.java" | xargs grep -in "TODO:" || true
	find -type f -name "*.c" | xargs grep -in "TODO:" || true
	find -type f -name "*.cpp" | xargs grep -in "TODO:" || true
	find -type f -name "*.h" | xargs grep -in "TODO:" || true
	cat TODO || true
	make cyclic

# (admin)
# when you write lesson for the future, write
# lesson:
lesson:
	find -type f -name "*.java" | xargs grep -in "lesson" || true
	find -type f -name "*.c" | xargs grep -in "lesson" || true
	find -type f -name "*.cpp" | xargs grep -in "lesson" || true
	find -type f -name "*.h" | xargs grep -in "lesson" || true

# (admin)
# constrast with the careers-cup book's code is after this symbol
# contrast:
contrast:
	find -type f -name "*.java" | xargs grep -in "contrast" || true
	find -type f -name "*.c" | xargs grep -in "constrast" || true
	find -type f -name "*.cpp" | xargs grep -in "contrast" || true
	find -type f -name "*.h" | xargs grep -in "contrast" || true

tree:
	find -type f -name "*.java" || true
	find -type f -name "*.h" || true
	find -type f -name "*.c" || true
	find -type f -name "*.cpp" || true

# example
# make find number=2.2
find:
	find -type f -name "*.java" | xargs grep -in $(number) || true
	find -type f -name "*.c" | xargs grep -in $(number) || true
	find -type f -name "*.cpp" | xargs grep -in $(number) || true
	find -type f -name "*.h" | xargs grep -in $(number) || true
