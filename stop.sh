#!/bin/bash

echo "Stopping Kafka (schema-registry-m3)..."
docker compose -f docker/compose/kafka-schema-registry-m3.yml down

echo "Stopping PostgreSQL..."
docker compose -f docker/compose/pg_compose.yml down

echo "All services stopped."
