package com.example.sensors.bme280;

import com.example.baseclasses.SendSensorData;
import com.example.configuration.HOSTConfiguration;
import com.example.configuration.PlainInfluxDB;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class BME280SendSensorDataInfluxDBPlainClient implements SendSensorData<BME280SensorData> {

	@Autowired
	private PlainInfluxDB plainInfluxDB;

	@Autowired
	private HOSTConfiguration hostConfiguration;

	@Override
	public void send(BME280SensorData sensorData) {
		String sensorName = sensorData.getClass().getSimpleName();
		String hostIpAddress = hostConfiguration.retrieveHOSTIPAddress();

		Point temperaturePoint = Point.measurement("ambient_temperature")
				.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
				.tag("host", hostIpAddress)
				.tag("sensor", sensorName)
				.addField("value", sensorData.getTemperature()).build();
		Point humidityPoint = Point.measurement("ambient_humidity")
				.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
				.tag("host", hostIpAddress)
				.tag("sensor", sensorName)
				.addField("value", sensorData.getHumidity()).build();
		Point pressurePoint = Point.measurement("ambient_pressure")
				.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
				.tag("host", hostIpAddress)
				.tag("sensor", sensorName)
				.addField("value", sensorData.getPressure()).build();

		InfluxDB influxDB = plainInfluxDB.getInfluxDB();
		influxDB.write(temperaturePoint);
		influxDB.write(humidityPoint);
		influxDB.write(pressurePoint);

	}
}
