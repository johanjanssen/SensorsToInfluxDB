package com.example;

import org.influxdb.BatchOptions;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class InfluxDBDemoIT {
    private static Logger logger = LoggerFactory.getLogger(InfluxDBDemoIT.class);

    private String databaseName = "testsensordata";

    private InfluxDB influxDB;

    /**
     * Remove all data between tests.
     */
    @Before
    public void cleanDatabase() {
        influxDB = InfluxDBFactory.connect("http://" + InfluxDBConfiguration.ipAddress + ":8086", "root", "root");
        Query dropDatabaseQuery = new Query("DROP DATABASE \"testsensordata\"", databaseName);
        influxDB.query(dropDatabaseQuery);

        Query createDatabaseQuery = new Query("CREATE DATABASE \"testsensordata\"", databaseName);
        influxDB.query(createDatabaseQuery);
    }

    @Test
    public void testImmediateWrite() {
        for (int i = 0; i < 10; i++) {
            Point point = Point.measurement("temperature")
                    .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                    .addField("value", i)
                    .build();
            influxDB.write(databaseName, "autogen", point);
        }

        final Query q = new Query("SELECT * FROM temperature", databaseName);

        Query countQuery = new Query("SELECT COUNT(\"value\") FROM \"temperature\"", databaseName);
        QueryResult queryResult = influxDB.query(countQuery);
        influxDB.close();
        assertEquals(10.0, queryResult.getResults().get(0).getSeries().get(0).getValues().get(0).get(1));
    }

    @Test
    public void testBatch() throws InterruptedException {
        // Flush every 6 Points, at least every 100ms
        influxDB.enableBatch(BatchOptions.DEFAULTS.actions(10).flushDuration(1000));

        for (int i = 0; i < 10; i++) {
            Point point = Point.measurement("temperature")
                    .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                    .addField("value", i)
                    .build();
            Thread.sleep(1);
            influxDB.write(databaseName, "autogen", point);
        }
        Thread.sleep(100);

        for (int i = 0; i < 9; i++) {
            Point point = Point.measurement("temperature")
                    .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                    .addField("value", i)
                    .build();
            Thread.sleep(1);
            influxDB.write(databaseName, "autogen", point);
        }

        Query countQuery = new Query("SELECT COUNT(\"value\") FROM \"temperature\"", databaseName);
        QueryResult queryResult = influxDB.query(countQuery);
        influxDB.close();
        logger.info(queryResult.toString());
        assertEquals(10.0, queryResult.getResults().get(0).getSeries().get(0).getValues().get(0).get(1));
    }
}
