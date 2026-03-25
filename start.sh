#!/bin/bash
set -e

echo "Starting Kafka (schema-registry-m3)..."
docker compose -f docker/compose/kafka-schema-registry-m3.yml up -d

echo "Starting PostgreSQL..."
docker compose -f docker/compose/pg_compose.yml up -d

echo "Waiting for services to be ready..."
sleep 10

echo "All services started."
