package com.example.sensors.flora;

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
public class FloraSendSensorDataInfluxDBClient implements SendSensorData<FloraSensorData> {

	@Autowired
	private InfluxDBTemplate<Point> influxDBTemplate;

	@Autowired
	private HOSTConfiguration hostConfiguration;

	@Override
	public void send(FloraSensorData sensorData) {
		String sensorName = sensorData.getClass().getSimpleName();
		String hostIpAddress = hostConfiguration.retrieveHOSTIPAddress();

		influxDBTemplate.createDatabase();
		final Point temperaturePoint = Point.measurement("ambient_temperature")
				.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
				.tag("host", hostIpAddress)
				.tag("sensor", sensorName)
				.addField("value", sensorData.getTemperature())
				.build();

		final Point batteryLevelPoint = Point.measurement("battery_level")
				.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
				.tag("host", hostIpAddress)
				.tag("sensor", sensorName)
				.addField("value", sensorData.getBatteryLevel())
				.build();

		final Point luxPoint = Point.measurement("lux")
				.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
				.tag("host", hostIpAddress)
				.tag("sensor", sensorName)
				.addField("value", sensorData.getLux())
				.build();

		final Point conductivityPoint = Point.measurement("conductivity")
				.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
				.tag("host", hostIpAddress)
				.tag("sensor", sensorName)
				.addField("value", sensorData.getConductivity())
				.build();

		final Point moisturePoint = Point.measurement("moisture")
				.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
				.tag("host", hostIpAddress)
				.tag("sensor", sensorName)
				.addField("value", sensorData.getMoisture())
				.build();


		List<Point> pointList = Arrays.asList(temperaturePoint, batteryLevelPoint, luxPoint, conductivityPoint, moisturePoint);

		influxDBTemplate.write(pointList);
	}
}
