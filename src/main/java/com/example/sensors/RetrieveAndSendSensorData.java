package com.example.sensors;

import com.example.baseclasses.RetrieveSensorData;
import com.example.baseclasses.SensorData;
import com.example.configuration.HOSTConfiguration;
import com.example.sensors.bme280.*;
import com.example.sensors.flora.*;
import com.example.sensors.hygrothermograph.*;
import com.example.sensors.raspberrypi.*;
import com.example.util.SensorException;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

@Component
public class RetrieveAndSendSensorData {
    private static Logger logger = LoggerFactory.getLogger(RetrieveAndSendSensorData.class);

    @Autowired
    private InfluxDB influxDB;

    @Value("${influxdb.javaclient.database}")
    private String databaseNamePlainJava;

    @Autowired
    private HOSTConfiguration hostConfiguration;

    // BME 280
    @Autowired
    private BME280RetrieveSensorData bme280RetrieveSensorData;

    @Autowired
    private BME280SendSensorDataInfluxDBClient bme280SendSensorDataInfluxDBClient;

    @Autowired
    private BME280SendSensorDataInfluxDBPlainClient bme280SendSensorDataInfluxDBPlainClient;

    @Autowired
    private BME280SendSensorDataInfluxDBHTTP bme280SendSensorDataInfluxDBHTTP;

    // Raspberry Pi
    @Autowired
    private RaspberryPiRetrieveSensorData raspberryPiRetrieveSensorData;

    @Autowired
    private RaspberryPiSendSensorDataInfluxDBClient raspberryPiSendSensorDataInfluxDBClient;

    @Autowired
    private RaspberryPiSendSensorDataInfluxDBPlainClient raspberryPiSendSensorDataInfluxDBPlainClient;

    @Autowired
    private RaspberryPiSendSensorDataInfluxDBHTTP raspberryPiSendSensorDataInfluxDBHTTP;

    // Hygrothermograph
    @Autowired
    private HygrothermographRetrieveSensorData retrieveHygrothermographSensorData;

    @Autowired
    private HygrothermographSendSensorDataInfluxDBClient hygrothermographSendSensorDataInfluxDBClient;

    @Autowired
    private HygrothermographSendSensorDataInfluxDBPlainClient hygrothermographSendSensorDataInfluxDBPlainClient;

    @Autowired
    private HygrothermographSendSensorDataInfluxDBHTTP hygrothermographSendSensorDataInfluxDBHTTP;

    // Flora
    @Autowired
    private FloraRetrieveSensorData retrieveFloraSensorData;

    @Autowired
    private FloraSendSensorDataInfluxDBClient floraSendSensorDataInfluxDBClient;

    @Autowired
    private FloraSendSensorDataInfluxDBPlainClient floraSendSensorDataInfluxDBPlainClient;

    @Autowired
    private FloraSendSensorDataInfluxDBHTTP floraSendSensorDataInfluxDBHTTP;

    public void start() {
        System.out.println("Time " + LocalTime.now());

        FloraSensorData floraSensorData = (FloraSensorData) retrieveSensorDataAndHandleException(retrieveFloraSensorData);
        HygrothermographSensorData hygrothermographSensorData = (HygrothermographSensorData) retrieveSensorDataAndHandleException(retrieveHygrothermographSensorData);
        RaspberryPiSensorData raspberryPiSensorData = (RaspberryPiSensorData) retrieveSensorDataAndHandleException(raspberryPiRetrieveSensorData);
        BME280SensorData bme280SensorData = (BME280SensorData) retrieveSensorDataAndHandleException(bme280RetrieveSensorData);

        printSensorData(floraSensorData);
        printSensorData(hygrothermographSensorData);
        printSensorData(raspberryPiSensorData);
        printSensorData(bme280SensorData);

        if (bme280SensorData != null) {
            bme280SendSensorDataInfluxDBClient.send(bme280SensorData);
            bme280SendSensorDataInfluxDBPlainClient.send(bme280SensorData);
            bme280SendSensorDataInfluxDBHTTP.send(bme280SensorData);
        }

        if (raspberryPiSensorData != null) {
            raspberryPiSendSensorDataInfluxDBClient.send(raspberryPiSensorData);
            raspberryPiSendSensorDataInfluxDBPlainClient.send(raspberryPiSensorData);
            raspberryPiSendSensorDataInfluxDBHTTP.send(raspberryPiSensorData);
        }

        if (hygrothermographSensorData != null) {
            hygrothermographSendSensorDataInfluxDBClient.send(hygrothermographSensorData);
            hygrothermographSendSensorDataInfluxDBPlainClient.send(hygrothermographSensorData);
            hygrothermographSendSensorDataInfluxDBHTTP.send(hygrothermographSensorData);
        }

        if (floraSensorData != null) {
            floraSendSensorDataInfluxDBClient.send(floraSensorData);
            floraSendSensorDataInfluxDBPlainClient.send(floraSensorData);
            floraSendSensorDataInfluxDBHTTP.send(floraSensorData);
        }
    }

    public SensorData retrieveSensorDataAndHandleException(RetrieveSensorData retrieveSensorData) {
        SensorData sensorData = null;
        LocalTime startTime = LocalTime.now();

        String hostIpAddress = hostConfiguration.retrieveHOSTIPAddress();

        try {
            sensorData = retrieveSensorData.retrieve();

            LocalTime endTime = LocalTime.now();
            long duration = Duration.between(startTime, endTime).toMillis();

            final Point durationPoint = Point.measurement("method_duration")
                    .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                    .tag("host", hostIpAddress)
                    .tag("sensor", retrieveSensorData.getClass().getSimpleName())
                    .addField("duration", duration)
                    .build();
            influxDB.write(databaseNamePlainJava, "autogen", durationPoint);

        } catch (SensorException e) {
            logger.error("Sensor exception: " + e.getOriginalClass() + " " + e.getMessage() + " " + e.getExtraInfo());

            final Point exceptionPoint = Point.measurement("exception")
                    .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                    .tag("host", hostIpAddress)
                    .tag("sensor", e.getOriginalClass())
                    .addField("message", e.getMessage())
                    .addField("extra_info", e.getExtraInfo())
                    .build();
            influxDB.write(databaseNamePlainJava, "autogen", exceptionPoint);
        }
        return sensorData;
    }


    public void printSensorData(SensorData sensorData) {
        if (sensorData != null) {
            System.out.println(sensorData.toString());
        }
    }
}
