package com.example.sensors.raspberrypi;

import com.example.baseclasses.SendSensorData;
import com.example.configuration.HOSTConfiguration;
import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RaspberryPiSendSensorDataInfluxDBPlainClient implements SendSensorData<RaspberryPiSensorData> {
	
	@Autowired
	private InfluxDB influxDB;

	@Autowired
	private HOSTConfiguration hostConfiguration;

	@Autowired
	private BatchPoints batchPoints;

	@Override
	public void send(RaspberryPiSensorData sensorData) {
		String sensorName = sensorData.getClass().getSimpleName();
		String hostIpAddress = hostConfiguration.retrieveHOSTIPAddress();

		Point cpuTemperaturePoint = Point.measurement("cpu_temperature")
				.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
				.tag("host", hostIpAddress)
				.tag("sensor", sensorName)
				.addField("value", sensorData.getCpuTemperature()).build();
		batchPoints.point(cpuTemperaturePoint);
		influxDB.write(batchPoints);
	}
}
