package com.example.sensors.bme280;


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
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations= "classpath:application-test.properties")
@ActiveProfiles("integration-test")
public class BME280SendSensorDataInfluxDBClientIT {

    @Autowired
    private BME280SendSensorDataInfluxDBClient bme280SendSensorDataInfluxDBClient;

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

        BME280SensorData bme280SensorData = new BME280SensorData(23.0, 2.1, 23423.4);
        bme280SendSensorDataInfluxDBClient.send(bme280SensorData);


        final Query q = new Query("SELECT * FROM ambient_temperature GROUP BY host", influxDBTemplate.getDatabase());
        QueryResult queryResult = influxDBTemplate.query(q);

        QueryResult.Series series = queryResult.getResults().get(0).getSeries().get(0);


        assertTrue(series.toString().startsWith("Series [name=ambient_temperature, tags={host="));

        assertEquals("ambient_temperature", series.getName());
        assertEquals(hostConfiguration.retrieveHOSTIPAddress(), series.getTags().get("host"));
        assertEquals("time", series.getColumns().get(0));
        assertEquals("sensor", series.getColumns().get(1));
        assertEquals("value", series.getColumns().get(2));

        assertEquals(1, series.getValues().size());
        assertEquals("BME280SensorData", series.getValues().get(0).get(1));
        assertEquals(23.0, series.getValues().get(0).get(2));
    }

    @Test
    public void testQueryResultToPOJO() {
        BME280SensorData bme280SensorData = new BME280SensorData(23.0, 2.1, 23423.4);
        bme280SendSensorDataInfluxDBClient.send(bme280SensorData);

        final Query q = new Query("SELECT * FROM ambient_temperature GROUP BY host", influxDBTemplate.getDatabase());
        QueryResult queryResult = influxDBTemplate.query(q);
        InfluxDBResultMapper resultMapper = new InfluxDBResultMapper(); // thread-safe - can be reused
        List<AmbientTemperature> cpuList = resultMapper.toPOJO(queryResult, AmbientTemperature.class);
        AmbientTemperature ambientTemperature = cpuList.get(0);
        assertEquals(Double.valueOf(23.0), ambientTemperature.getValue());
        assertEquals(hostConfiguration.retrieveHOSTIPAddress(), ambientTemperature.getHost());
    }
}
