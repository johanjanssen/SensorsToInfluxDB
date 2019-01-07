package com.example.util;

public class SensorException extends RuntimeException {
    private String originalClass;
    private String extraInfo = "";

    public SensorException(String message, String originalClass, String extraInfo) {
        super(message);
        this.originalClass = originalClass;
        this.extraInfo = extraInfo;
    }

    public SensorException(String message, String originalClass) {
        super(message);
        this.originalClass = originalClass;
    }

    public String getOriginalClass() {
        return originalClass;
    }

    public String getExtraInfo() {
        return extraInfo;
    }
}
