#!/bin/bash

# Run with spark-submit
/home/anish/spark/bin/spark-submit \
  --class BasicSparkJob \
  --master local[*] \
  target/scala-2.12/spark-scala-k8s_2.12-0.1.0-SNAPSHOT.jar