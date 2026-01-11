#!/bin/bash

READERS=${1:-10}
WRITERS=${2:-3}

echo "=========================================================="
echo " Starting simulation"
echo " Number of Readers: $READERS"
echo " Number of Writers: $WRITERS"
echo "=========================================================="

mvn -q clean compile exec:java \
    -Dexec.mainClass="readers_and_writers.Main" \
    -Dexec.args="$READERS $WRITERS"
