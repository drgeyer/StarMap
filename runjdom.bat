REM Windoze batch file to compile and run Java programs that use JDOM
REM Usage: runjdom javafile(no ext) xmlfile
REM JMW 141029

javac -cp .;jdom.jar starmap/StarMap.java
java  -cp .;jdom.jar starmap/StarMap stars.xml constellations.xml
