Cassandra
=========


### Install
```bash
brew install cassandra
```


### Start
```bash
brew services start cassandra
tail -f /usr/local/var/log/cassandra/system.log
```
> Via Docker compose
```bash
# just start only single service
docker-compose  -f docker-compose-infra.yml up cassandra
# start all infra services
docker-compose  -f docker-compose-infra.yml
```


### CQL
```cql
cqlsh
# cqlsh 10.205.105.152 9042 -u bdpaasloader -p Notr3DaWnIte
cqlsh> SELECT cluster_name, listen_address FROM system.local;
cqlsh> CREATE KEYSPACE sample WITH replication = {'class':'SimpleStrategy','replication_factor':1};
cqlsh> use sample;
cqlsh> CREATE TABLE emp (empid int primary key, emp_first varchar, emp_last varchar, emp_dept varchar);
cqlsh> INSERT INTO emp (empid, emp_first, emp_last, emp_dept) values (1,'fred','smith','eng');
cqlsh> update emp set emp_dept = 'fin' where empid = 1;
cqlsh> select * from emp where empid = 1;
cqlsh> CREATE INDEX idx_dept on emp(emp_dept);
 
 CREATE TABLE IF NOT EXISTS  sample.hotels (
     id UUID,
     name varchar,
     address varchar,
     zip varchar,
     version int,
     primary key((id))
 );
```
