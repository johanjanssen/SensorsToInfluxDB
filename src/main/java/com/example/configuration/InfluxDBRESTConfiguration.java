package com.example.configuration;

import org.influxdb.InfluxDB;
import org.influxdb.dto.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class InfluxDBRESTConfiguration {

    @Value("${influxdb.rest.database}")
    private String databaseName;

    @Autowired
    private InfluxDB influxDB;

    @PostConstruct
    public void createDatabase() {
        Query createDatabaseQuery = new Query("CREATE DATABASE " + databaseName, databaseName);
        influxDB.query(createDatabaseQuery);
    }
}
