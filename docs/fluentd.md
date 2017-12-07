EFK Demo
========
A basic docker-compose file that will set up Elasticsearch, Fluentd, and Kibana

shows how to configure a service to use EFK as its logging facility. To test using this file, just run:

```bash
docker-compose -f docker-compose-fluentd.yml up
```

Then, go to your browser and access `http://localhost:80` (httpd) and `http://localhost:5601` (kibana). 
You should be able to see the httpd's logs in kibana's discovery tab. 
By the way, if you are wondering what is this index kibana asks the fist time you access it, it is `fluentd-*`.

After you are done, just run:
```bash
docker-compose -f docker-compose-fluentd.yml rm -f
```

And all services will be reclaimed.
