#!/bin/bash
echo "shutting down confluent kafka..."
SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )
docker-compose -f $SCRIPT_DIR/kafka-schema-registry.yml kill
docker-compose -f $SCRIPT_DIR/kafka-schema-registry.yml rm -f
echo "Done."
