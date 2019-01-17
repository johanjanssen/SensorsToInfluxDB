package com.example.sensors.raspberrypi;

import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

@Measurement(name="cpu_temperature")
public class CPUTemperature {
    @Column(name = "host", tag = true)
    private String host;

    @Column(name = "sensor", tag = true)
    private String sensor;

    @Column(name="value")
    private Double value;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getSensor() {
        return sensor;
    }

    public void setSensor(String sensor) {
        this.sensor = sensor;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
