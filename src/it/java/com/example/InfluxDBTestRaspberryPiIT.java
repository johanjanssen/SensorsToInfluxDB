package com.example;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Automated test to verify that the data from all sensors is stored in the database.
 */
public class InfluxDBTestRaspberryPiIT {

    private static Logger logger = LoggerFactory.getLogger(InfluxDBTestRaspberryPiIT.class);

    private InfluxDB influxDB;

    private String databaseNameJavaClient = "JavaClientSensorData";

    private String databaseNameREST = "RESTSensorData";

    private String databaseNameSpringTemplate = "SpringTemplateSensorData";

    private int numberOfResultsExpected = 2;

    @Before
    public void connectDatabase() {
        influxDB = InfluxDBFactory.connect("http://" + InfluxDBConfiguration.ipAddressRaspberryPi + ":8086", "root", "root");

    }

    @Test
    public void testJavaClient() {
        testClient(databaseNameJavaClient);
    }

    @Test
    public void testRESTClient() {
        testClient(databaseNameREST);
    }

    @Test
    public void testSpringTemplateClient() {
        testClient(databaseNameSpringTemplate);
    }

    private void testClient(String databaseName) {
        Map<QueryResult, String> queryResultList = new HashMap<>();

        // BME280
        Query countQueryAmbientTemperatureBME280 = new Query("SELECT COUNT(\"value\") FROM \"ambient_temperature\" WHERE sensor = 'BME280SensorData'", databaseName);
        QueryResult queryResultAmbientTemperatureBME280 = influxDB.query(countQueryAmbientTemperatureBME280);
        queryResultList.put(queryResultAmbientTemperatureBME280, "BME280SensorData: ambient_temperature");

        Query countQueryAmbientHumidityBME280 = new Query("SELECT COUNT(\"value\") FROM \"ambient_humidity\" WHERE sensor = 'BME280SensorData'", databaseName);
        QueryResult queryResultAmbientHumidityBME280 = influxDB.query(countQueryAmbientHumidityBME280);
        queryResultList.put(queryResultAmbientHumidityBME280, "BME280SensorData: ambient_humidity");

        Query countQueryAmbientPressureBME280 = new Query("SELECT COUNT(\"value\") FROM \"ambient_pressure\" WHERE sensor = 'BME280SensorData'", databaseName);
        QueryResult queryResultAmbientPressureBME280 = influxDB.query(countQueryAmbientPressureBME280);
        queryResultList.put(queryResultAmbientPressureBME280, "BME280SensorData: ambient_pressure");

        // Raspberry Pi
        Query countQueryCPUCelciusRPI = new Query("SELECT COUNT(\"value\") FROM \"cpu_temperature\" WHERE sensor = 'RaspberryPiSensorData'", databaseName);
        QueryResult queryResultCPUCelciusRPI = influxDB.query(countQueryCPUCelciusRPI);
        queryResultList.put(queryResultCPUCelciusRPI, "RaspberryPiSensorData: cpu_temperature");

        // Flora
        Query countQueryAmbientTemperatureFlora = new Query("SELECT COUNT(\"value\") FROM \"ambient_temperature\" WHERE sensor = 'FloraSensorData'", databaseName);
        QueryResult queryResultAmbientTemperatureFlora = influxDB.query(countQueryAmbientTemperatureFlora);
        queryResultList.put(queryResultAmbientTemperatureFlora, "FloraSensorData: ambient_temperature");

        Query countQueryBatteryLevelFlora = new Query("SELECT COUNT(\"value\") FROM \"battery_level\" WHERE sensor = 'FloraSensorData'", databaseName);
        QueryResult queryResultBatteryLevelFlora = influxDB.query(countQueryBatteryLevelFlora);
        queryResultList.put(queryResultBatteryLevelFlora, "FloraSensorData: battery_level");

        Query countQueryLuxFlora = new Query("SELECT COUNT(\"value\") FROM \"lux\" WHERE sensor = 'FloraSensorData'", databaseName);
        QueryResult queryResultLuxFlora = influxDB.query(countQueryLuxFlora);
        queryResultList.put(queryResultLuxFlora, "FloraSensorData: lux");

        Query countQueryConductivityFlora = new Query("SELECT COUNT(\"value\") FROM \"conductivity\" WHERE sensor = 'FloraSensorData'", databaseName);
        QueryResult queryResultConductivityFlora = influxDB.query(countQueryConductivityFlora);
        queryResultList.put(queryResultConductivityFlora, "FloraSensorData: conductivity");

        Query countQueryMoistureFlora = new Query("SELECT COUNT(\"value\") FROM \"moisture\" WHERE sensor = 'FloraSensorData'", databaseName);
        QueryResult queryResultMoistureFlora = influxDB.query(countQueryMoistureFlora);
        queryResultList.put(queryResultMoistureFlora, "FloraSensorData: moisture");

        // Hygrothermograph
        Query countQueryAmbientTemperatureHygrothermograph = new Query("SELECT COUNT(\"value\") FROM \"ambient_temperature\" WHERE sensor = 'HygrothermographSensorData'", databaseName);
        QueryResult queryResultAmbientTemperatureHygrothermograph = influxDB.query(countQueryAmbientTemperatureHygrothermograph);
        queryResultList.put(queryResultAmbientTemperatureHygrothermograph, "HygrothermographSensorData: ambient_temperature");

        Query countQueryBatteryLevelHygrothermograph = new Query("SELECT COUNT(\"value\") FROM \"battery_level\" WHERE sensor = 'HygrothermographSensorData'", databaseName);
        QueryResult queryResultBatteryLevelHygrothermograph = influxDB.query(countQueryBatteryLevelHygrothermograph);
        queryResultList.put(queryResultBatteryLevelHygrothermograph, "HygrothermographSensorData: battery_level");

        Query countQueryAmbientHumidityHygrothermograph = new Query("SELECT COUNT(\"value\") FROM \"ambient_humidity\" WHERE sensor = 'HygrothermographSensorData'", databaseName);
        QueryResult queryResultAmbientHumidityHygrothermograph = influxDB.query(countQueryAmbientHumidityHygrothermograph);
        queryResultList.put(queryResultAmbientHumidityHygrothermograph, "HygrothermographSensorData: ambient_humidity");

        influxDB.close();
        for (QueryResult queryResult : queryResultList.keySet()) {
            assertNumberOfRows(queryResult, databaseName, queryResultList.get(queryResult));
        }

    }

    private void assertNumberOfRows(QueryResult queryResult, String databaseName, String field) {
        logger.info("Database: " + databaseName + " result: " + queryResult.toString());
        assertNotNull("Result was empty for database: " + databaseName + " field: " + field, queryResult.getResults().get(0).getSeries());
        double numberOfRows = Double.valueOf(queryResult.getResults().get(0).getSeries().get(0).getValues().get(0).get(1).toString());
        assertTrue("Number of rows: " + numberOfRows + " for database: " + databaseName, numberOfRows > numberOfResultsExpected);
    }
}
