kafka InfluxDB Service
======================
kafka InfluxDB connector

High performance Kafka to InfluxDB connector. Supports`logstash` message formats.

Kafka will serve as a buffer for your logs data during high load. 

This component can scale horizontally on platforms like Mesos. 

![Architecture](../docs/kafka-logs-influxdb.png "Log Flow")


##### Technology stack
* Spring Boot 2.0.0
* Reactor Kafka

##### Features
 

### Run
> use `./gradlew` instead of `gradle` if you didn't installed `gradle`
```bash
gradle kafka-influxdb-service:bootRun
# add new tags
INFLUXDB_TAGS=HOSTNAME,app,level,level_value gradle kafka-influxdb-service:bootRun
# run with `docker` profile. 
SPRING_PROFILES_ACTIVE=docker gradle kafka-influxdb-service:bootRun
```

### Test
```bash
gradle kafka-influxdb-service:test
```

### Build
```bash
gradle kafka-influxdb-service:build
# skip test
gradle kafka-influxdb-service:build -x test 
# build docker image
gradle kafka-influxdb-service:docker -x test 
```

### Deploy
> deploying app to Cloud
```bash
# tag and puch to cassandra-data-service
docker login
docker push
```

### Verify

Access InfluxDB URL:
> http://localhost:8083/
```sql
select * from logs where time > now() - 1m order by time desc limit 10;
```
