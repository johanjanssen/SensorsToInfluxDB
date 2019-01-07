package com.example.sensors.flora;

import com.example.util.LinuxCommand;
import com.example.baseclasses.RetrieveSensorData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FloraRetrieveSensorData implements RetrieveSensorData {

    private static Logger logger = LoggerFactory.getLogger(FloraRetrieveSensorData.class);

    @Value("${flora.bluetoothaddress}")
    private String bluetoothAddress;

    public String getBluetoothAddress() {
        return bluetoothAddress;
    }

    public FloraSensorData retrieve() {
        FloraSensorData floraSensorData = tempAndHumidity();
        floraSensorData.setBatteryLevel(batteryLevel());
        return floraSensorData;
    }


    private int batteryLevel() {
        String command = "gatttool -b " + bluetoothAddress + " --char-read --handle=0x038 | cut -c 34-35";
        String result = LinuxCommand.executeCommand(command, this.getClass().getSimpleName());
        return Integer.parseInt(result, 16);
    }

    public static FloraSensorData convertUnixResultToJava(String data) {
        if ("Characteristic value/descriptor: aa bb cc dd ee ff 99 88 77 66 00 00 00 00 00 00".equals(data)) {
            logger.error("Received default response from Flora sensor");
            return new FloraSensorData();
        }
        String tempHex = data.substring(33, 38);
        String tempHexReverse = tempHex.substring(3, 5) + tempHex.substring(0, 2);
        Double temperature = Integer.parseInt(tempHexReverse, 16) / 10.0;

        String moistureHex = data.substring(54, 56);
        Integer moisture = Integer.parseInt(moistureHex, 16);

        String luxHex = data.substring(42, 54);
        String luxHexReverse = luxHex.substring(9, 11) + luxHex.substring(6, 8) + luxHex.substring(3, 5) + luxHex.substring(0, 2);
        Integer lux = Integer.parseInt(luxHexReverse, 16);

        String conductivityHex = data.substring(57, 62);
        String conductivityHexReverse = conductivityHex.substring(3, 5) + conductivityHex.substring(0, 2);
        Integer conductivity = Integer.parseInt(conductivityHexReverse, 16);

        FloraSensorData floraSensorData = new FloraSensorData(temperature, lux, moisture, conductivity);

        return floraSensorData;
    }

    private FloraSensorData tempAndHumidity() {
        String writeCommand = "gatttool -b " + bluetoothAddress + " --char-write-req -a 0x0033 -n A01F";
        String readCommand = "gatttool -b " + bluetoothAddress + " --char-read -a 0x0035";
        LinuxCommand.executeCommand(writeCommand, this.getClass().getSimpleName());
        String result = LinuxCommand.executeCommand(readCommand, this.getClass().getSimpleName());
        return convertUnixResultToJava(result);
    }


}
