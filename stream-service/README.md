Stream Service
=============
Stream API

##### Technology stack
* Spring Boot 2.0.0
* Spring WebFlux
* Server-Sent Events (SSE) 

##### Features
* Functional Style Routes
* Use of Server-Sent Events (SSE) rendered in HTML by Thymeleaf from a reactive data stream.
* Use of Server-Sent Events (SSE) rendered in JSON by Spring WebFlux from a reactive data stream.
* Reactive Netty as a server
* Dockerized - Docker deployment

### Run
> use `./gradlew` instead of `gradle` if you didn't installed `gradle`
```bash
gradle stream-service:bootRun
# run with `docker` profile. 
SPRING_PROFILES_ACTIVE=docker gradle stream-service:bootRun
```
### Test
```bash
gradle stream-service:test
```
### Build
```bash
gradle stream-service:build -x test 
# continuous build with `-t`. 
gradle -t stream-service:build
# build docker image
gradle stream-service:docker -x test 
```

### Deploy
> deploying app to Cloud
```bash
# tag and puch to cassandra-data-service
docker login
docker push
```


### API
http://localhost:8082/sse/quotes


http://localhost:8082/sse/fibonacci


http://localhost:8082/sse/logs

### Test testing tool for socket.io

http://amritb.github.io/socketio-client-tool/

Connect URL: http://localhost:8082/websocket/echo [TODO]
