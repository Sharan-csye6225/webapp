#!/bin/bash
echo "Intallation Depedency Script file execution started."

sudo yum install -y java-17-openjdk-devel

java -version

echo "JDK install completed"

echo 'export JAVA_HOME=/usr/lib/jvm/java-17-openjdk' >>~/.bashrc
echo 'export PATH=$JAVA_HOME/bin:$PATH' >>~/.bashrc

cat ~/.bashrc
sudo source ~/.bashrc

cd /opt/
sudo mkdir cloud/
ls -al
pwd

