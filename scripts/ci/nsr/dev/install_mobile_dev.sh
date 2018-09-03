#!/bin/bash -xe

# run unit tests and generate profiling (for late on be send to JaCoCo)
./gradlew clean :nsr:test --profile