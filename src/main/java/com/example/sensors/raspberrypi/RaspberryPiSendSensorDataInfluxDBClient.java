package com.example.sensors.raspberrypi;

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
public class RaspberryPiSendSensorDataInfluxDBClient implements SendSensorData<RaspberryPiSensorData> {

	@Autowired
	private InfluxDBTemplate<Point> influxDBTemplate;

	@Autowired
	private HOSTConfiguration hostConfiguration;

	@Override
	public void send(RaspberryPiSensorData sensorData) {
		String sensorName = sensorData.getClass().getSimpleName();
		String hostIpAddress = hostConfiguration.retrieveHOSTIPAddress();

		influxDBTemplate.createDatabase();
		final Point temperaturePoint = Point.measurement("cpu_temperature")
				.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
				.tag("host", hostIpAddress)
				.tag("sensor", sensorName)
				.addField("value", sensorData.getCpuTemperature())
				.build();

		List<Point> pointList = Arrays.asList(temperaturePoint);
		influxDBTemplate.write(pointList);
	}
}
