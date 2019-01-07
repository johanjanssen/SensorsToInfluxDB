package com.example.sensors.bme280;

import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

@Measurement(name="ambient_temperature")
public class AmbientTemperature {
    @Column(name = "host", tag = true)
    private String host;

    @Column(name="value")
    private Double value;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
