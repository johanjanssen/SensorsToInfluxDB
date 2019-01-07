package com.example.sensors.hygrothermograph;

import com.example.baseclasses.SendSensorData;
import com.example.configuration.HOSTConfiguration;
import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class HygrothermographSendSensorDataInfluxDBPlainClient implements SendSensorData<HygrothermographSensorData> {

	@Autowired
	private InfluxDB influxDB;
	
	@Autowired
	private BatchPoints batchPoints;

	@Autowired
	private HOSTConfiguration hostConfiguration;

	@Override
	public void send(HygrothermographSensorData sensorData) {
		String sensorName = sensorData.getClass().getSimpleName();
		String hostIpAddress = hostConfiguration.retrieveHOSTIPAddress();

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

		final Point humidityPoint = Point.measurement("ambient_humidity")
				.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
				.tag("host", hostIpAddress)
				.tag("sensor", sensorName)
				.addField("value", sensorData.getHumidity())
				.build();

		batchPoints.point(temperaturePoint);
		batchPoints.point(batteryLevelPoint);
		batchPoints.point(humidityPoint);
		influxDB.write(batchPoints);
	}
}
