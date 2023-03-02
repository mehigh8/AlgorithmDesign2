# Rosu Mihai Cosmin 323CA

build: Curatare.java Curse.java Beamdrone.java Fortificatii.java
	javac Curatare.java
	javac Curse.java
	javac Beamdrone.java
	javac Fortificatii.java
	
run-p1:
	java Curatare
	
run-p2:
	java Fortificatii
	
run-p3:
	java Beamdrone
	
run-p4:
	java Curse
	
clean:
	rm -f *.class
	
.PHONY: build clean
