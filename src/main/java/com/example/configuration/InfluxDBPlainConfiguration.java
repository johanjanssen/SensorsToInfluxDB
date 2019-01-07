package com.example.configuration;

import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class InfluxDBPlainConfiguration {

	@Value("${influxdb.javaclient.database}")
	private String databaseName;

	@Autowired
	private InfluxDB influxDB;

	@PostConstruct
	public void createDatabase() {
		Query createDatabaseQuery = new Query("CREATE DATABASE " + databaseName, databaseName);
		influxDB.query(createDatabaseQuery);
	}

	@Bean
	public BatchPoints createBatchPoints() {
		return BatchPoints.database(databaseName).consistency(InfluxDB.ConsistencyLevel.ALL)
				.build();
	}

}
