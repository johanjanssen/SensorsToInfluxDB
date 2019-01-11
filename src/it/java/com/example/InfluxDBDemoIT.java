package com.example;

import com.example.configuration.PlainInfluxDB;
import org.influxdb.BatchOptions;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations= "classpath:application-test.properties")
@ActiveProfiles("integration-test")
public class InfluxDBDemoIT {
    private static Logger logger = LoggerFactory.getLogger(InfluxDBDemoIT.class);

    private String databaseName = "testsensordata";

    @Autowired
    private PlainInfluxDB plainInfluxDB;
    /**
     * Remove all data between tests.
     */
    @Before
    public void cleanDatabase() {
        Query dropDatabaseQuery = new Query("DROP DATABASE \"testsensordata\"", databaseName);
        plainInfluxDB.getInfluxDB().query(dropDatabaseQuery);

        Query createDatabaseQuery = new Query("CREATE DATABASE \"testsensordata\"", databaseName);
        plainInfluxDB.getInfluxDB().query(createDatabaseQuery);
    }


    @Test
    public void testTraditionalQuery() {
        initializeData();
        Query query = new Query("SELECT value FROM ambient_temperature WHERE sensor = 'BME280'", databaseName);
        QueryResult queryResult = plainInfluxDB.getInfluxDB().query(query);
        String expectedResult = "QueryResult [results=[Result [series=[Series [name=ambient_temperature, tags=null, columns=[time, value], values=[[2019-01-09T09:07:55.81Z, 0.0], [2019-01-09T09:07:55.811Z, 1.0]]]], error=null]], error=null]";
        assertEquals(expectedResult, queryResult.toString());
    }

    @Test
    public void testTraditionalQueryREST() throws UnsupportedEncodingException {
        initializeData();
        RestTemplate restTemplate = new RestTemplate();

        String database = URLEncoder.encode("db=testsensordata", "UTF-8");
        String query = URLEncoder.encode("SELECT value FROM ambient_temperature", "UTF-8");
        String url = "http://localhost:8086/query?pretty=true&db=testsensordata&q="+ query;

        ResponseEntity<String> result = restTemplate.getForEntity(url, String.class);
        System.out.println(result.getBody());
    }

    private void initializeData() {
        System.out.println(System.currentTimeMillis());
        for (int i = 0; i < 2; i++) {
            Point point = Point.measurement("ambient_temperature")
                    .time(1547024875810L + i, TimeUnit.MILLISECONDS) // Fixed time for easy of testing
                    .addField("value", i)
                    .tag("sensor", "BME280")
                    .build();
            plainInfluxDB.getInfluxDB().write(point);
        }
        for (int i = 0; i < 2; i++) {
            Point point = Point.measurement("ambient_temperature")
                    .time(1547024875810L + i, TimeUnit.MILLISECONDS) // Fixed time for easy of testing
                    .addField("value", i)
                    .tag("sensor", "Hygrothermograph")
                    .build();
            plainInfluxDB.getInfluxDB().write(point);
        }
    }

    @Test
    public void testImmediateWrite() {
        for (int i = 0; i < 10; i++) {
            Point point = Point.measurement("temperature")
                    .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                    .addField("value", i)
                    .build();
            plainInfluxDB.getInfluxDB().write(point);

        }

        Query countQuery = new Query("SELECT COUNT(\"value\") FROM \"temperature\"", databaseName);
        QueryResult queryResult = plainInfluxDB.getInfluxDB().query(countQuery);
        plainInfluxDB.getInfluxDB().close();
        assertEquals(10.0, queryResult.getResults().get(0).getSeries().get(0).getValues().get(0).get(1));
    }

    @Test
    public void testBatch() throws InterruptedException {
        // Flush every 10 Points, at least every 1000ms
        plainInfluxDB.getInfluxDB().enableBatch(BatchOptions.DEFAULTS.actions(10).flushDuration(1000));

        for (int i = 0; i < 10; i++) {
            Point point = Point.measurement("temperature")
                    .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                    .addField("value", i)
                    .build();
            Thread.sleep(1);
            plainInfluxDB.getInfluxDB().write( point);
        }
        Thread.sleep(100);

        for (int i = 0; i < 9; i++) {
            Point point = Point.measurement("temperature")
                    .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                    .addField("value", i)
                    .build();
            Thread.sleep(1);
            plainInfluxDB.getInfluxDB().write(point);
        }

        Query countQuery = new Query("SELECT COUNT(\"value\") FROM \"temperature\"", databaseName);
        QueryResult queryResult = plainInfluxDB.getInfluxDB().query(countQuery);
        plainInfluxDB.getInfluxDB().close();
        logger.info(queryResult.toString());
        assertEquals(10.0, queryResult.getResults().get(0).getSeries().get(0).getValues().get(0).get(1));
    }
}
