Grafana
=======

### Start
Start Grafana and other Infra components via docker compose
```bash
# just start only single service
docker-compose  -f docker-compose-infra.yml up grafana
# start all infra services
docker-compose  -f docker-compose-infra.yml

```

> Adding Data Source to Grafana 

```bash
## Add DataSource into Grafana
curl -X "POST" "http://localhost:3000/api/datasources" \
	    -H "Content-Type: application/json" \
	     --user admin:admin \
	     -d $'{"id":1,"orgId":1,"name":"'$table_name'","type":"elasticsearch","typeLogoUrl":"public/app/plugins/datasource/elasticsearch/img/elasticsearch.svg","access":"proxy","url":"http://localhost:9200","password":"","user":"","database":"'$table_name'","basicAuth":false,"isDefault":false,"jsonData":{"timeField":"EVENT_TS"}}'
```
> Loading AppLogs Dashboard to Grafana
```bash
curl -X "POST" "http://localhost:3000/api/dashboards/db" \
	    -H "Content-Type: application/json" \
	     --user admin:admin \
	     --data-binary @infra/grafana/dashboards/AppLogs-Dashboard.json
```
