package com.example.sensors.raspberrypi;


import com.example.configuration.HOSTConfiguration;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.impl.InfluxDBResultMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.influxdb.InfluxDBTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations= "classpath:application-test.properties")
@ActiveProfiles("integration-test")
public class RaspberryPiSendSensorDataInfluxDBClientIT {

    @Autowired
    private RaspberryPiSendSensorDataInfluxDBClient raspberryPiSendSensorDataInfluxDBClient;

    @Autowired
    private InfluxDBTemplate<Point> influxDBTemplate;

    @Autowired
    private HOSTConfiguration hostConfiguration;

    /**
     * Remove all data between tests.
     */
    @Before
    public void cleanDatabase() {
        Query dropDatabaseQuery = new Query("DROP DATABASE \"testsensordata\"", influxDBTemplate.getDatabase());
        influxDBTemplate.query(dropDatabaseQuery);

        Query createDatabaseQuery = new Query("CREATE DATABASE \"testsensordata\"", influxDBTemplate.getDatabase());
        influxDBTemplate.query(createDatabaseQuery);
    }

    @Test
    public void testSend() {
        assertEquals("testsensordata", influxDBTemplate.getDatabase());

        RaspberryPiSensorData raspberryPiSensorData = new RaspberryPiSensorData(23.4);
        raspberryPiSendSensorDataInfluxDBClient.send(raspberryPiSensorData);


        final Query q = new Query("SELECT * FROM cpu_temperature GROUP BY host, sensor", influxDBTemplate.getDatabase());
        QueryResult queryResult = influxDBTemplate.query(q);

        QueryResult.Series series = queryResult.getResults().get(0).getSeries().get(0);

        assertEquals("cpu_temperature", series.getName());
        assertEquals(hostConfiguration.retrieveHOSTIPAddress(), series.getTags().get("host"));
        assertEquals("RaspberryPiSensorData", series.getTags().get("sensor"));
        assertEquals("time", series.getColumns().get(0));
        assertEquals("value", series.getColumns().get(1));

        assertEquals(23.4, series.getValues().get(0).get(1));
    }

    @Test
    public void testQueryResultToPOJO() {
        RaspberryPiSensorData raspberryPiSensorData = new RaspberryPiSensorData(23.4);
        raspberryPiSendSensorDataInfluxDBClient.send(raspberryPiSensorData);

        final Query q = new Query("SELECT * FROM cpu_temperature GROUP BY host", influxDBTemplate.getDatabase());
        QueryResult queryResult = influxDBTemplate.query(q);
        InfluxDBResultMapper resultMapper = new InfluxDBResultMapper(); // thread-safe - can be reused
        List<CPUTemperature> cpuList = resultMapper.toPOJO(queryResult, CPUTemperature.class);
        CPUTemperature cpuTemperature = cpuList.get(0);
        assertEquals(Double.valueOf(23.4), cpuTemperature.getValue());
        assertEquals(hostConfiguration.retrieveHOSTIPAddress(), cpuTemperature.getHost());
        assertEquals("RaspberryPiSensorData", cpuTemperature.getSensor());
    }
}
