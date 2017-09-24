INSERT INTO ticks (pair_day,issued_at,bid,ask) VALUES ('usdjpy:2011-02-03','2011-02-03 04:05:13.020+0000',0.12,0.13) USING TTL 3600;


INSERT INTO ticks_avg_s (pair_day,issued_at,bid,ask) VALUES ('usdjpy:2011-02-03','2011-02-03 04:05:00.000+0000',0.12,0.13) USING TTL 3600;