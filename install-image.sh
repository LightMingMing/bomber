#!/bin/bash

./gradlew compileJava

CP=build/classes/java/main

rm -f jettyShutdown

# generate an image to speed up shutdown
native-image -cp $CP --enable-url-protocols=http com.bomber.JettyShutdown jettyShutdown

rm -f jettyShutdown.o