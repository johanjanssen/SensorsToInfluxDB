package com.example.sensors.hygrothermograph;

import com.example.baseclasses.SensorData;

public class HygrothermographSensorData implements SensorData {
    private double temperature;
    private double humidity;
    private int batteryLevel;

    public HygrothermographSensorData(double temperature, double humidity, int batteryLevel) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.batteryLevel = batteryLevel;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }

    @Override
    public String toString() {
        return "HygrothermographSensorData{" +
                "temperature=" + temperature +
                ", humidity=" + humidity +
                ", batteryLevel=" + batteryLevel +
                '}';
    }
}
