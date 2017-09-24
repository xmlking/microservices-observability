Docker
======
Docker Cheat Sheet.

### Install
Install `Docker for Mac` app [Installation](https://docs.docker.com/docker-for-mac/install/)

Add project root to `Docker for Mac` preferences so that `infra` directory is available for for Docker Engine. 
> `Docker for Mac` --> Preferences --> File Sharing --> add project root

### Docker Commands

```bash
# To see list of images
docker images
docker images -a
# To delete an image
docker rmi  eb46b3df6e36 
docker rmi  eb46b3df6e36 -f
# To run an image 
# Start cassandra and check logs
docker run -p 9042:9042 --name sumo-cassandra -it cassandra:latest
docker logs sumo-cassandra
docker exec -it sumo-cassandra bash

# Start cassandra-data-service with docker Profile and link wigh cassandra
docker run -p 8081:8080 -e "SPRING_PROFILES_ACTIVE=docker" --link sumo-cassandra:cassandra -i -t microservices-observability/cassandra-data-service:0.1.0-SNAPSHOT

docker run -p 8082:8082 -it microservices-observability/stream-service:0.1.0-SNAPSHOT

# Using Spring Profiles
docker run -e "SPRING_PROFILES_ACTIVE=docker" -p 8082:8080 -i -t microservices-observability/stream-service:0.1.0-SNAPSHOT

# Debugging the application in a Docker container
docker run -e "JAVA_OPTS=-agentlib:jdwp=transport=dt_socket,address=5005,server=y,suspend=n" -p 8082:8080 -p 5005:5005 -i -t microservices-observability/stream-service:0.1.0-SNAPSHOT
# To see list of running containers
docker ps
# To stop a running container
docker stop 81c723d22865
# SSH to the running container (CONTAINER ID from `docker ps` command)
docker exec -i <CONTAINER ID> sh
# To check logs
docker logs sumo-cassandra
# inspect a docker image
docker inspect confluentinc/ksql-cli
```

##### Docker Compose
```bash
# start containers in the background
docker-compose up -d
# start containers in the foreground
docker-compose up 
# show runnning containers 
docker-compose ps
# scaling containers and load balancing
docker-compose scale stream=3
# 1. stop the running containers using
docker-compose stop
# 2. remove the stopped containers using
docker-compose rm -f
# connect(ssh) to a service and run a command
docker-compose exec cassandra cqlsh
# see logs of a service 
docker-compose logs -f docker-compose-infra.yml cassandra
# just start only infra services
docker-compose  -f docker-compose-infra.yml up
# just start only single service
docker-compose  -f docker-compose-infra.yml up alertmanager
# restart single service
docker-compose  -f docker-compose-infra.yml restart alertmanager
```

### Maintenance
```bash
docker container prune
docker image prune
docker network prune
docker volume prune
# will delete ALL unused data (i.e. In order: containers stopped, volumes without containers and images with no containers).
docker system prune
```

### Reference
* https://clusterhq.com/2016/03/09/fun-with-swarm-part1/
