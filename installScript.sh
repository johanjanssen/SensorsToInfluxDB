# Update Raspbian
sudo apt-get update

# Install i2c tools for BME280 sensor
sudo apt-get install -y i2c-tools -y

# Compile and install WiringPi for Pi4J
# WORKS
#sudo apt-get install git-core -y
#git clone git://git.drogon.net/wiringPi
#cd ~/wiringPi
#./build
# HAVE TO TEST:
sudo apt-get install wiringpi

# Install Java 8
# For now since Pi4J has issues on 11
sudo apt-get install openjdk-8-jdk -y

# Install Java 11
#wget https://github.com/bell-sw/Liberica/releases/download/11.0.1/bellsoft-jdk11.0.1-linux-arm32-vfp-hflt.tar.gz
#tar -xf bellsoft-jdk*tar.gz
#export PATH=$PWD/jdk-11.0.1/bin:$PATH
#rm bellsoft-jdk*tar.gz

# Install Maven
wget http://apache.40b.nl/maven/maven-3/3.6.0/binaries/apache-maven-3.6.0-bin.tar.gz
sudo tar xzvf apache-maven-3.6.0-bin.tar.gz -C /opt
export PATH=/opt/apache-maven-3.6.0/bin:$PATH

# Install Docker
curl -fsSL get.docker.com -o get-docker.sh && sh get-docker.sh
rm get-docker.sh

# Adding the current user to the docker group so sudo is no longer needed to run docker commands
sudo gpasswd -a $USER docker
newgrp docker

# Install Docker Compose
sudo apt-get install python-pip -y
sudo pip install docker-compose

# Download project
sudo apt-get install git -y
git clone

# Pre build the project so the Maven dependencies are downloaded
cd SensorsToInfluxDB
mvn package

# Pre download Docker images
cd DockerRaspberryPi
docker-compose pull
