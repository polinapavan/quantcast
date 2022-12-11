#!/bin/sh

java -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=1111 -jar cookies-processor-1.0-SNAPSHOT-jar-with-dependencies.jar $1 $2 $3 $4