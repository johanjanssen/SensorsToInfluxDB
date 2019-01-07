## Introduction
DISCLAIMER: I know the code can be optimized (duplicate code etcetera). However I use it for demo purposes and tried to make the code as easy to understand as possible thereby sacrificing maintainability.

This project reads data from various sensors:
- BME 280
- Raspberry Pi temperature sensor
- Xiaomi Flora bluetooth sensor
- Xiaomi Hygrothermograph bluetooth sensor

The data is then send to an InfluxDB database in three ways:
- InfluxDB REST api
- InfluxDB Java client
- InfluxDB Spring Template

The data can be shown on a Chronograf or Grafana dashboard.

Don't forget to configure the *.properties files with your own information such as IP addresses and bluetooth addresses.

## Setup the Raspberry Pi

### Installation
- Download Raspbian Lite from https://www.raspberrypi.org/downloads/raspbian/ (tested with Stretch)
- Install Raspbian on a Micro SD card
- Enable SSH by placing a file called 'ssh' without extension on the boot (smaller) partition of the SD card

### Optional configure WIFI
- Connect the Raspberry Pi with a LAN cable
- Add the WIFI router credentials to /etc/wpa_supplicant/wpa_supplicant.conf
        ```bash
        network={
            ssid="[routername]"
            psk="[WPA password]"
        }
        ```

### Enabling I<sup>2</sup>C for BME 280
- Enable the I<sup>2</sup>C interface
    - Execute the command ```sudo raspi-config```
    - Select ```Interfacing options```
    - Enable the I<sup>2</sup>C interface
    - Select Finish

### Install the necessary software
- Copy the installScript.sh to the Raspberry Pi
- Execute the script: 
    ```bash
    sh installScript.sh
    ```
- After a logout and login you can use docker without sudo 

### Start Docker containers with InfluxDB, Chronograf and Grafana
- Open directory: DockerRaspberryPi
- Execute command: docker-compose up

### Run the application
- Create a JAR file
    ```bash
    mvn package
    ```
- Run the application on the Raspberry Pi
    ```bash
    java -jar *.jar
    ```
- Run the application on the Raspberry Pi in the background so you can close the shell
    ```bash
    nohup java -jar *.jar > log.txt 2>&1 &
    ```

    

## InfluxDB
Some more information, not directly needed to work with the example.
### Connect to the database
- Connect to the InfluxDB container
	```bash
    sudo docker exec -it influxdb /usr/bin/influx
    ```
- Create a database:
    ```bash
    CREATE DATABASE mydb
    ```
- Show the available databases
    ```bash
    SHOW DATABASES
    ```
- Select which database you want to use
    ```bash
    USE mydb
    ```
- Insert some test values in the database
    ```bash
    INSERT test,tag1=testtag value=42
    INSERT test,tag1=testtag value=21
    INSERT test,tag1=testtag value=84
    ```
- Show the content of the database
    ```bash
    SELECT * FROM test
    ```
- Type ```quit``` to exit from the InfluxDB container

