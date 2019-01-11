package com.example.configuration;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Query;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InfluxDBPlainConfiguration {

	@Value("${influxdb.javaclient.database}")
	private String databaseName;

	@Value("${spring.influx.url}")
	private String influxDBURL;

	/**
	 * Wrap InfluxDB so this can be used for plain InfluxDB interaction and isn't used for the InfluxDBTemplate.
	 */
	@Bean
	public PlainInfluxDB initializePlainInfluxDB() {
		InfluxDB influxDB = InfluxDBFactory.connect(influxDBURL, "root", "root");
		Query createDatabaseQuery = new Query("CREATE DATABASE " + databaseName, databaseName);
		influxDB.query(createDatabaseQuery);
		influxDB.setDatabase(databaseName);
		PlainInfluxDB plainInfluxDB = new PlainInfluxDB(influxDB);
		return plainInfluxDB;
	}
}
