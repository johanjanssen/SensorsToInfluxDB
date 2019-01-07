package com.example.sensors.bme280;

import com.example.baseclasses.SendSensorData;
import com.example.configuration.HOSTConfiguration;
import org.influxdb.dto.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.influxdb.InfluxDBTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class BME280SendSensorDataInfluxDBClient implements SendSensorData<BME280SensorData> {

	@Autowired
	private InfluxDBTemplate<Point> influxDBTemplate;

	@Autowired
	private HOSTConfiguration hostConfiguration;

	@Override
	public void send(BME280SensorData sensorData) {
		String sensorName = sensorData.getClass().getSimpleName();
		String hostIpAddress = hostConfiguration.retrieveHOSTIPAddress();

		influxDBTemplate.createDatabase();
		final Point temperaturePoint = Point.measurement("ambient_temperature")
				.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
				.tag("host", hostIpAddress)
				.tag("sensor", sensorName)
				.addField("value", sensorData.getTemperature())
				.build();

		final Point humidityPoint = Point.measurement("ambient_humidity")
				.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
				.tag("host", hostIpAddress)
				.tag("sensor", sensorName)
				.addField("value", sensorData.getHumidity())
				.build();

		final Point pressurePoint = Point.measurement("ambient_pressure")
				.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
				.tag("host", hostIpAddress)
				.tag("sensor", sensorName)
				.addField("value", sensorData.getPressure())
				.build();


		List<Point> pointList = Arrays.asList(temperaturePoint, humidityPoint, pressurePoint);

		influxDBTemplate.write(pointList);
	}
}
