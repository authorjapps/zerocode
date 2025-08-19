#!/bin/bash
# Gets the source of the swagger petstore.
# After this, execute the command to run the container
SCRIPT_DIR=$(readlink -f $0 | xargs dirname)
echo "run command from directory: $SCRIPT_DIR"
cd $SCRIPT_DIR/../../../..
mkdir target
cd target
pwd
git clone https://github.com/swagger-api/swagger-petstore --branch swagger-petstore-v3-1.0.19 
