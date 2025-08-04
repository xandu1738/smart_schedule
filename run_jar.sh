#!/bin/bash

SCRIPT_DIR=$(dirname "$0")
source "$SCRIPT_DIR/.env"

cd "$SCRIPT_DIR/front" && npm install

npm run build

rm -rf "$SCRIPT_DIR/src/main/resources/static"

mv "$SCRIPT_DIR/front/dist" "$SCRIPT_DIR/src/main/resources/static"

cd "$SCRIPT_DIR" && mvn clean package -DskipTests

java \
  -Dspring.profiles.active="${SPRING_PROFILE}" \
  -Dspring.datasource.url="${DB_URL}" \
  -Dspring.datasource.password="${DB_PASSWORD}" \
  -Dserver.port="${SERVER_PORT}" \
  -Xms256m -Xmx256m \
  -jar "$SCRIPT_DIR/target/smart_schedule.jar"
