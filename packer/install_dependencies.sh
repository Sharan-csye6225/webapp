#!/bin/bash
echo "Intallation Depedency Script file execution started."

# Install MySQL (assuming MySQL is what you want, not MariaDB)
sudo yum install -y mysql-server

echo "MySQL install completed"

# Start MySQL service
sudo systemctl start mysqld

# Enable MySQL to start on boot
sudo systemctl enable mysqld

sudo mysql -u root <<EOF
ALTER USER 'root'@'localhost' IDENTIFIED BY 'root';
FLUSH PRIVILEGES;
exit
EOF

# Install Java JDK 17
sudo yum install -y java-17-openjdk-devel

#To see the Java version installed
java -version

echo "JDK install completed"

# Install Maven
sudo yum install -y maven

#To see the Maven version installed
mvn -version

echo "Maven install completed"

echo 'export JAVA_HOME=/usr/lib/jvm/java-17-openjdk' >>~/.bash_profile
echo 'export PATH=$JAVA_HOME/bin:$PATH' >>~/.bash_profile

echo 'export M2_HOME=/opt/mavenk' >>~/.bash_profile
echo 'export PATH=$M2_HOME/bin:$PATH' >>~/.bash_profile

source ~/.bash_profile

cd /opt/
sudo mkdir cloud/
ls -al
pwd

