package com.example.sensors.flora;

import com.example.baseclasses.SensorData;

public class FloraSensorData implements SensorData {

    private double temperature;

    private int lux;

    private int moisture;

    private int conductivity;

    private int batteryLevel;

    public FloraSensorData() {
    }

    public FloraSensorData(double temperature, int lux, int moisture, int conductivity) {
        this.temperature = temperature;
        this.lux = lux;
        this.moisture = moisture;
        this.conductivity = conductivity;
    }

    public double getTemperature() {
        return temperature;
    }


    public int getLux() {
        return lux;
    }

    public int getMoisture() {
        return moisture;
    }

    public int getConductivity() {
        return conductivity;
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(int batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    @Override
    public String toString() {
        return "FloraSensorData{" +
                "temperature=" + temperature +
                ", lux=" + lux +
                ", moisture=" + moisture +
                ", conductivity=" + conductivity +
                ", batteryLevel=" + batteryLevel +
                '}';
    }
}
