CREATE KEYSPACE sample WITH replication = {
  'class': 'SimpleStrategy',
  'replication_factor': '1'
};

USE sample;

CREATE TABLE ticks (
pair_day text,
issued_at timestamp,
bid float,
ask float,
PRIMARY KEY (pair_day, issued_at)
) WITH compaction = {'class' : 'LeveledCompactionStrategy' };


CREATE TABLE ticks_avg_s (
pair_day text,
issued_at timestamp,
bid float,
ask float,
PRIMARY KEY (pair_day, issued_at)
) WITH compaction = {'class' : 'LeveledCompactionStrategy' };

