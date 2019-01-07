package com.example.sensors.raspberrypi;

import com.example.baseclasses.SensorData;

public class RaspberryPiSensorData implements SensorData {

	private double cpuTemperature;

	public RaspberryPiSensorData(double cpuTemperature) {
		super();
		this.cpuTemperature = cpuTemperature;
	}

	public double getCpuTemperature() {
		return cpuTemperature;
	}

	public void setCpuTemperature(double cpuTemperature) {
		this.cpuTemperature = cpuTemperature;
	}

	@Override
	public String toString() {
		return "RaspberryPiSensorData{" +
				"cpuTemperature=" + cpuTemperature +
				'}';
	}
}
