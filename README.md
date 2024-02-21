# This is repo has webapp project code

# Application Build and Deployment Guide

## Prerequisites

**Before building and deploying your application locally, ensure you have the following prerequisites installed**:

- **JDK (Java Development Kit) 17**: Install JDK 17 on your local machine. You can download it from [Oracle's website](https://www.oracle.com/java/technologies/javase-jdk17-downloads.html) or use OpenJDK.

  On Linux (Ubuntu), you can install OpenJDK 17 using the following command:
  sudo apt install openjdk-17-jdk

  **Set the "JAVA_HOME" in the ".bash_profile" file**
	  export JAVA_HOME=/usr/lib/jvm/java-17-openjdk
	  export PATH=$JAVA_HOME/bin:$PATH

- **Apache Maven**: Make sure you have Apache Maven installed to build the application. You can download it from Maven's official website.

	On Linux (Ubuntu), you can install Maven using the following command:
	sudo apt install maven

	**Set the "JAVA_HOME" in the ".bash_profile" file**
		export M2_HOME=/opt/maven
		export PATH=$M2_HOME/bin:$PATH

- **MySQL Database**: If your application requires a database, ensure that MySQL Database Server is installed and running locally. You can download MySQL from MySQL's official website.

	On Linux (Ubuntu), you can install MySQL Server using the following command:
	sudo apt install mysql-server

**Build and Deploy Instructions**:-
	**Clone the Repository**:  git clone <repository-url>
	**Navigate to the Project Directory**: cd <project-directory>
	**Configure Database Connection (if applicable)**: Make sure to configure the database connection properties in the application.properties or application.yml file located in the **src/main/resources** directory of your project.
	**Build the Application**: mvn clean install
	**Run the Application**: mvn spring-boot:run
		Alternatively, An executable JAR file generated in the target directory:  java -jar target/<your-application>.jar
	**Access the Application**: Once the application is running locally, you can access it using a web browser. Open your web browser and navigate to http://localhost:8080 to view the application.
	

**List of commands used for building and running the project on CentOS (DigitalOcean)**:-

CentOS Droplet (VM) -    **.***.***.***
ssh-keygen	-> ssh key generation
cat /Users/sharankumar/.ssh/digitalocean.pub  -> To view the public key and copy & paste in the droplet (DigitalOcean), when creating them.
ssh -i /Users/sharankumar/.ssh/digitalocean root@146.190.125.130 	-> Command to ssh into the droplet.
chmod 600 .ssh/digitalocean    -> To give correct permission to the private key and allows connection.
scp -i .ssh/digitalocean combined.txt root@64.23.173.105:/tmp    -> Scp is used to securely copy the file to the droplet (VM) server.

**Set of Commands to be executed for Application Build and Deployment inside DigitalOcean CentOS droplet**:-

# Install unzip
sudo yum install -y unzip

echo "Unzip install completed"

# Install Java JDK 17
sudo yum install -y java-17-openjdk-devel

echo "JDK install completed"

#To see the Java version installed
java -version

# Install Maven
sudo yum install -y maven

echo "Maven install completed"

# Install MySQL (assuming MySQL is what you want, not MariaDB)
sudo yum install -y mysql-server

echo "MySQL install completed"

# Start MySQL service
sudo systemctl start mysqld

# Enable MySQL to start on boot
sudo systemctl enable mysqld

# To set password for the root user
ALTER USER 'root'@'localhost' IDENTIFIED BY ‘root’;
FLUSH PRIVILEGES;
exit;
