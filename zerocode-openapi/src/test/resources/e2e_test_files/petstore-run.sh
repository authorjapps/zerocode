#!/bin/bash
# run the container with the swagger petstore api at port 8080
# Before executing this, run the clone command to have the sources in the target/swagger-petstore folder.
SCRIPT_DIR=$(readlink -f $0 | xargs dirname)
echo "run command from directory: $SCRIPT_DIR"
cd $SCRIPT_DIR/../../../../target/swagger-petstore
pwd
mvn package -ntp -DskipTests=true
docker build -t swagger-petstore .
docker stop swagger-petstore && docker rm swagger-petstore
docker run -d -p 8080:8080 --name swagger-petstore swagger-petstore
