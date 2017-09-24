#!/bin/bash
set -e

echo "Provisioning Grafana with datasource and dashboards"
grafana_url=http://localhost:3000

curl --fail -u admin:admin -X POST -H "Content-Type: application/json"  -d @- $grafana_url/api/datasources <<EOF
{
  "name":"Prometheus",
  "type":"prometheus",
  "url":"http://localhost:9090",
  "Access":"direct",
  "basicAuth":false
}
EOF

echo
dashboards=$(dirname $0)/dashboards/*
for dashboard in $dashboards
do
  echo "Adding dashboard $dashboard"
  bodyjson=`cat $dashboard`
  curl --fail -u admin:admin -X POST -H "Content-Type: application/json" -d '{ "dashboard": '"$bodyjson"' , "overwrite": false }' $grafana_url/api/dashboards/db
  echo
done