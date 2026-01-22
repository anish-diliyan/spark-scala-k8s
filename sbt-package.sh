#!/bin/bash

# Use JAVA_HOME from environment
export PATH=$JAVA_HOME/bin:$PATH

# Build the JAR
sbt clean compile package