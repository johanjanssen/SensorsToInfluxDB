package com.example.sensors.bme280;

import com.example.baseclasses.SensorData;

public class BME280SensorData implements SensorData {
	private double temperature;
	private double humidity;
	private double pressure;
	
	public BME280SensorData(double temperature, double humidity, double pressure) {
		super();
		this.temperature = temperature;
		this.humidity = humidity;
		this.pressure = pressure;
	}

	public double getTemperature() {
		return temperature;
	}

	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}

	public double getHumidity() {
		return humidity;
	}

	public void setHumidity(double humidity) {
		this.humidity = humidity;
	}

	public double getPressure() {
		return pressure;
	}

	public void setPressure(double pressure) {
		this.pressure = pressure;
	}

	@Override
	public String toString() {
		return "BME280SensorData{" +
				"temperature=" + temperature +
				", humidity=" + humidity +
				", pressure=" + pressure +
				'}';
	}
}
