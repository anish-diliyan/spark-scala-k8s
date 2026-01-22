# Spark Scala Kubernetes Project

## Prerequisites

- Java 17
- SBT
- Apache Spark 3.5.3
- Kubernetes (for deployment)

## Setup

1. Clone the repository
2. Ensure Java 17 is installed and JAVA_HOME is set
3. Install SBT and Apache Spark

## Build

```bash
./scripts/build.sh
```

## Run Locally

```bash
./scripts/run-spark-job.sh
```

## Project Structure

- `src/main/scala/` - Scala source code
- `scripts/` - Build and run scripts
- `target/` - Compiled artifacts (ignored by git)