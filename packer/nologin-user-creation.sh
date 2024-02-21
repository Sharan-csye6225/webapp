#!/bin/bash

sudo cp /tmp/csye6225-0.0.1-SNAPSHOT.jar /opt/cloud/csye6225-0.0.1-SNAPSHOT.jar
sudo cp /tmp/.env /opt/cloud/.env
sudo groupadd csye6225
sudo useradd -s /usr/sbin/nologin -g csye6225 -d /opt/cloud -M csye6225
sudo chown -R csye6225:csye6225 /opt/cloud
sudo chmod 750  /opt/cloud/csye6225-0.0.1-SNAPSHOT.jar
cd /opt/cloud/ && ls -al
pwd

sudo cp /tmp/webservice.service /etc/systemd/system
sudo systemctl daemon-reload
sudo systemctl start webservice.service
sudo systemctl enable webservice.service
sudo systemctl restart webservice.service
sudo systemctl status webservice.service