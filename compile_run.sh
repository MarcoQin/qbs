#!/bin/bash
cd src/

javac -classpath ../lib/ffmpeg-macosx-x86_64.jar:../lib/ffmpeg.jar:../lib/javacpp.jar:../lib/javacv.jar:../lib/opencv-macosx-x86_64.jar:../lib/opencv.jar  CanvasFrame.java GBC.java ScreenGrabber.java Settings.java

jar cvfm ../qbs.jar MANIFEST.MF  *.class ../lib/*.jar

cd ..

java -jar qbs.jar
