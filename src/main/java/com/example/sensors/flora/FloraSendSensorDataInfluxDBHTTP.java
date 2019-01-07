package com.example.sensors.flora;

import com.example.baseclasses.SendSensorData;
import com.example.configuration.HOSTConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class FloraSendSensorDataInfluxDBHTTP implements SendSensorData<FloraSensorData> {
	
	@Autowired
	private RestTemplate restTemplate;

	@Value("${influxdb.rest.url}")
	private String restURL;

	@Autowired
	private HOSTConfiguration hostConfiguration;

	@Override
	public void send(FloraSensorData sensorData) {
		String sensorName = sensorData.getClass().getSimpleName();
		String hostIpAddress = hostConfiguration.retrieveHOSTIPAddress();

		String temperature = "ambient_temperature,host=" + hostIpAddress + ",sensor=" + sensorName + " value=" + sensorData.getTemperature();
		String batteryLevel = "\nbattery_level,host=" + hostIpAddress + ",sensor=" + sensorName + " value=" + sensorData.getBatteryLevel();
		String lux = "\nlux,host=" + hostIpAddress + ",sensor=" + sensorName + "  value=" + sensorData.getLux();
		String conductivity = "\nconductivity,host=" + hostIpAddress + ",sensor=" + sensorName + "  value=" + sensorData.getConductivity();
		String moisture = "\nmoisture,host=" + hostIpAddress + ",sensor=" + sensorName + "  value=" + sensorData.getMoisture();

		String measurement = temperature + batteryLevel + lux + conductivity + moisture;
		
		restTemplate.postForLocation(restURL, measurement);
	}
}
