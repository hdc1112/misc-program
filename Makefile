.PHONY: clean

clean:
	find -type f -name "*~" | xargs rm -rf || true
	find -type f -name "*stackdump" | xargs rm -rf || true
	find -type f -name "*.class" | xargs rm -rf || true
	find -type f -name "*.h.gch" | xargs rm -rf || true
	find -type f -name "*.exe" | xargs rm -rf || true
	find -type f -name "*.out" | xargs rm -rf || true
