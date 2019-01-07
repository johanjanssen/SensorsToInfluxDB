package com.example.sensors.flora;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FloraRetrieveSensorDataTest {

    /*
     * All in little endian
     * Temperature 0-1 in 0.1 degrees celcius d6 00
     * Light 3-6 in lux 6b 01 00 00
     * Moisture 7 in % 12
     * Conductivity 8-9 mu S/cm da 00
     */
    private String example = "Characteristic value/descriptor: d6 00 00 6b 01 00 00 12 da 00 02 3c 00 fb 34 9b";

    @Test
    public void testConvertUnixResultToJava() {
        FloraSensorData floraSensorData = FloraRetrieveSensorData.convertUnixResultToJava(example);

        assertEquals(21.4, floraSensorData.getTemperature(), 0.0);

        assertEquals(18, floraSensorData.getMoisture());

        assertEquals(363, floraSensorData.getLux());

        assertEquals(218, floraSensorData.getConductivity());
    }
}
