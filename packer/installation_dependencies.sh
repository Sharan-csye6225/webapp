#!/bin/bash
echo "Intallation Depedency Script file execution started."

#sudo yum update -y

sudo yum install -y mysql-server

echo "MySQL install completed"

sudo systemctl start mysqld

sudo systemctl enable mysqld

sudo mysql -u root <<EOF
ALTER USER 'root'@'localhost' IDENTIFIED BY 'root';
FLUSH PRIVILEGES;
exit
EOF

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

