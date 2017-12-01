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

* http://localhost:8080/actuator/info
* http://localhost:8080/actuator/health
* http://localhost:8080/actuator/metrics
* http://localhost:8080/actuator/prometheus
* http://localhost:8080/actuator/env 
* http://localhost:8080/actuator/beans
* http://localhost:8080/actuator/loggers
* http://localhost:8080/actuator/mappings
* http://localhost:8080/actuator/auditevents
* http://localhost:8080/actuator/autoconfig
* http://localhost:8080/actuator/configprops
* http://localhost:8080/actuator/threaddump
* http://localhost:8080/actuator/heapdump
* http://localhost:8080/actuator/trace
