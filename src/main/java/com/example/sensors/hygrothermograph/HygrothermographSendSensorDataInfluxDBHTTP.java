package com.example.sensors.hygrothermograph;

import com.example.baseclasses.SendSensorData;
import com.example.configuration.HOSTConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class HygrothermographSendSensorDataInfluxDBHTTP implements SendSensorData<HygrothermographSensorData> {
	
	@Autowired
	private RestTemplate restTemplate;

	@Value("${influxdb.rest.url}")
	private String restURL;

	@Autowired
	private HOSTConfiguration hostConfiguration;

	@Override
	public void send(HygrothermographSensorData sensorData) {
		String sensorName = sensorData.getClass().getSimpleName();
		String hostIpAddress = hostConfiguration.retrieveHOSTIPAddress();

		String temperature = "ambient_temperature,host=" + hostIpAddress + ",sensor=" + sensorName + " value=" + sensorData.getTemperature();
		String batteryLevel = "\nbattery_level,host=" + hostIpAddress + ",sensor=" + sensorName + " value=" + sensorData.getBatteryLevel();
		String humidity = "\nambient_humidity,host=" + hostIpAddress + ",sensor=" + sensorName + "  value=" + sensorData.getHumidity();


		String measurement = temperature + batteryLevel + humidity;
		
		restTemplate.postForLocation(restURL, measurement);
	}
}
