package com.example.configuration;

import org.influxdb.InfluxDB;

/**
 * Used to wrap InfluxDB so this can be used for plain InfluxDB interaction and isn't used for the InfluxDBTemplate.
 */
public class PlainInfluxDB {

    private InfluxDB influxDB;

    public PlainInfluxDB(InfluxDB influxDB) {
        this.influxDB = influxDB;
    }

    public InfluxDB getInfluxDB() {
        return influxDB;
    }
}
