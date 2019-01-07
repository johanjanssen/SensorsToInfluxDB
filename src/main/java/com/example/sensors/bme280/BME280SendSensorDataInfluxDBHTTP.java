package com.example.sensors.bme280;

import com.example.baseclasses.SendSensorData;
import com.example.configuration.HOSTConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class BME280SendSensorDataInfluxDBHTTP implements SendSensorData<BME280SensorData> {
	
	@Autowired
	private RestTemplate restTemplate;

	@Value("${influxdb.rest.url}")
	private String restURL;

	@Autowired
	private HOSTConfiguration hostConfiguration;

	@Override
	public void send(BME280SensorData sensorData) {
		String sensorName = sensorData.getClass().getSimpleName();
		String hostIpAddress = hostConfiguration.retrieveHOSTIPAddress();

		String temperature = "ambient_temperature,host=" + hostIpAddress + ",sensor=" + sensorName + " value=" + sensorData.getTemperature();
		String humidity = "\nambient_humidity,host=" + hostIpAddress + ",sensor=" + sensorName + " value=" + sensorData.getHumidity();
		String pressure = "\nambient_pressure,host=" + hostIpAddress + ",sensor=" + sensorName + "  value=" + sensorData.getPressure();
		String measurement = temperature + humidity + pressure;
		
		restTemplate.postForLocation(restURL, measurement);
	}
}
