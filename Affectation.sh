#!/bin/bash

# Define the network name
network_name="spring-mysql"

# Check if the network exists, and if not, create it
if ! docker network inspect "$network_name" >/dev/null 2>&1; then
  echo "Creating network: $network_name"
  docker network create "$network_name"
fi

# Start the first container
docker container run --name affectationsqldb --network spring-mysql -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=AffectationDatabase -d mysql:8.0.33

# Wait for the desired log message to appear
echo "Waiting for MySQL to start..."
while true; do
  if docker logs affectationsqldb | grep -q "MySQL init process done. Ready for start up."; then
    break
  fi
  echo please wait for my sql setup 
  sleep 10
done

# Start the second container after the desired message appears in the logs of the first container
docker container run --name affectationspringbackend -p 9090:9090 --network=spring-mysql -e MYSQL_HOST=affectationsqldb  -e MYSQL_USER=root -e  MYSQL_PASSWORD=root -e  MYSQL_PORT=3306 -e MYSQL_Strategy=create   -d tayeblagha/affectationbackend
sleep 3
docker container run  --name=affectationangularfront  -p 4200:4200 --network=spring-mysql   -d tayeblagha/affectationfrontend
# Wait for the desired log message to appear
clear
echo "Waiting for Angular container ..."

while true; do
  if docker logs affectationangularfront | grep -q "Compiled successfully."; then
    break
  fi
  sleep 10
  echo "Waiting for Angular container ..."
done

docker ps

