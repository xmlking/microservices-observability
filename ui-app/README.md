UI App
======
UI Client App

##### Technology stack
* Spring Boot
* Spring WebFlux

##### Features
* Traditional Annotation-based Routes  
* WebClient
* Use of Thymeleaf's fully-HTML5-compatible syntax.
* Use of webjars for client-side dependency managements.
* Cross-Origin Resource Sharing (CORS)
* Dockerized - Docker deployment

### Run
> use `./gradlew` instead of `gradle` if you didn't installed `gradle`
```bash
gradle ui-app:bootRun
# run with `docker` profile.
SPRING_PROFILES_ACTIVE=docker gradle ui-app:bootRun
```
### Test
```bash
gradle ui-app:test
```
### Build
```bash
gradle ui-app:build
# skip test
gradle ui-app:build -x test
# build docker image
gradle ui-app:docker -x test 
```

### Deploy
> deploying app to Cloud
```bash
# tag and puch to cassandra-data-service
docker login
docker push
```

### App
* http://localhost:8080

#### Actuator End Points

* http://localhost:8080/application/info
* http://localhost:8080/application/health
* http://localhost:8080/application/metrics
* http://localhost:8080/application/prometheus
* http://localhost:8080/application/env 
* http://localhost:8080/application/beans
* http://localhost:8080/application/loggers
* http://localhost:8080/application/mappings
* http://localhost:8080/application/auditevents
* http://localhost:8080/application/autoconfig
* http://localhost:8080/application/configprops
* http://localhost:8080/application/threaddump
* http://localhost:8080/application/heapdump
* http://localhost:8080/application/trace
