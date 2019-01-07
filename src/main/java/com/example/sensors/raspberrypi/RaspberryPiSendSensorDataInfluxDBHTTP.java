package com.example.sensors.raspberrypi;

import com.example.baseclasses.SendSensorData;
import com.example.configuration.HOSTConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RaspberryPiSendSensorDataInfluxDBHTTP implements SendSensorData<RaspberryPiSensorData> {

	@Autowired
	private RestTemplate restTemplate;

	@Value("${influxdb.rest.url}")
	private String restURL;

	@Autowired
	private HOSTConfiguration hostConfiguration;

	@Override
	public void send(RaspberryPiSensorData sensorData) {
		String sensorName = sensorData.getClass().getSimpleName();
		String hostIpAddress = hostConfiguration.retrieveHOSTIPAddress();

		String measurement = "cpu_temperature,host=" + hostIpAddress + ",sensor=" + sensorName + " value=" + sensorData.getCpuTemperature();
		
		restTemplate.postForLocation(restURL, measurement);
	}
}
