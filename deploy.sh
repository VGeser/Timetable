#!/bin/bash

set -e

export DOCKER_CONTEXT=sanya

./gradlew bootJar
docker compose -f docker-compose.production.yml up -d --build --remove-orphans