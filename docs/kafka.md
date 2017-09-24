Kafka  
=====


### Start
Start Kafka and other Infra components via docker compose
```bash
# just start only single service
docker-compose  -f docker-compose-infra.yml up kafka
# start all infra services
docker-compose  -f docker-compose-infra.yml
```
 
### Test
> 
```bash
# do you have local kafka binaries? assume you installed confluent kafka on your laptop at $KAFKA_HOME
export KAFKA_HOME=/Developer/Applications/confluent-3.3.0
$KAFKA_HOME/bin/kafka-console-consumer --bootstrap-server localhost:9092 --topic applogs --from-beginning --property print.key=true
# or if you are using docker env, connect to kafka container:
docker-compose -f docker-compose-infra.yml exec kafka bash
# list topics
kafka-topics --list --zookeeper zookeeper:2181
# send messages
kafka-console-producer --broker-list kafka:9092 --topic applogs
# receive messages
kafka-console-consumer --bootstrap-server kafka:9092 --topic applogs --from-beginning --property print.key=true
```

> Kafka KSQL Server 

```bash
docker-compose  -f docker-compose-kafka.yml up
docker-compose  -f docker-compose-kafka.yml exec ksql-server ksql-cli remote http://localhost:9098

docker-compose  -f docker-compose-kafka.yml exec ksql-server bash
docker-compose  -f docker-compose-kafka.yml ps
docker-compose  -f docker-compose-kafka.yml logs -f ksql-server 
docker-compose  -f docker-compose-kafka.yml restart ksql-server

ksql> run script '/usr/share/doc/ksql-clickstream-demo/clickstream-schema.sql';

SHOW TABLES;
SHOW STREAMS;
SHOW QUERIES;

DROP STREAM my_logs_stream;
CREATE STREAM my_logs_stream (timestamp varchar, version bigint, message varchar, logger_name varchar, thread_name varchar, level varchar, level_value bigint, HOSTNAME varchar, app varchar) WITH (kafka_topic='applogs', value_format='JSON');
DESCRIBE my_logs_stream;

DROP TABLE my_logs;
CREATE TABLE my_logs (timestamp varchar, version bigint, message varchar, logger_name varchar, thread_name varchar, level varchar, level_value bigint, HOSTNAME varchar, app varchar) WITH (kafka_topic='applogs', value_format='JSON');
DESCRIBE my_logs;

SELECT * FROM  my_logs;
SELECT ROWTIME, app,  timestamp, version FROM my_logs_stream LIMIT 3;
```


### Reference
* http://www.landoop.com/blog/2016/12/kafka-influxdb/ 
* https://github.com/sqshq/ELK-docker
* https://github.com/stefanprodan/dockprom

