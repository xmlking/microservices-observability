Cassandra Data Service
======================
Cassandra Data API

##### Technology stack
* Spring Boot 2.0.0
* Spring WebFlux
* Reactive Cassandra Driver
* Integration tests with Docker Compose

##### Features
* Functional Style Routes
* CORS Enabled 
* Dockerized 
* Use of Spring Data Cassandra

### Run
> use `./gradlew` instead of `gradle` if you didn't installed `gradle`
```bash
gradle cassandra-data-service:bootRun
# run with `docker` profile.
SPRING_PROFILES_ACTIVE=docker gradle cassandra-data-service:bootRun
```
### Test
```bash
# start the dependent containers first... (TODO: issue with starting  delay of  cassandra)
docker-compose -f docker-compose-infra.yml up cassandra
docker-compose -f docker-compose-infra.yml up kafka
# run the tests
gradle cassandra-data-service:test
```
### Build
```bash
gradle cassandra-data-service:build
# skip test
gradle cassandra-data-service:build -x test 
# build docker image
gradle cassandra-data-service:docker -x test 
```

### Deploy
> deploying app to Cloud
```bash
# tag and puch to cassandra-data-service
docker login
docker push
```

### API

#### Local API 
* http://localhost:8081/api/guestbook
* http://localhost:8081/api/guestbook/Jack%20Bauer
* http://localhost:8081/api/guestbook/name/Jack%20Bauer

##### EventSource API
* http://localhost:8081/sse/guestbook/Sumo

### Ref
* http://docs.spring.io/spring-data/cassandra/docs/current/reference/html/
