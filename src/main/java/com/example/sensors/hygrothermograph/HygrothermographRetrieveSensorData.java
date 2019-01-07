package com.example.sensors.hygrothermograph;

import com.example.util.LinuxCommand;
import com.example.baseclasses.RetrieveSensorData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HygrothermographRetrieveSensorData implements RetrieveSensorData {
    @Value("${hygrothermograph.bluetoothaddress}")
    private String bluetoothAddress;

    public HygrothermographSensorData retrieve() {
        String[] tempAndHumidity = tempAndHumidity();
        double temperature = Double.valueOf(tempAndHumidity[0]);
        double humidity = Double.valueOf(tempAndHumidity[1]);
        int batteryLevel = batteryLevel();

        return new HygrothermographSensorData(temperature, humidity, batteryLevel);
    }

    private int batteryLevel() {
        String command = "gatttool -b " + bluetoothAddress + " --char-read --handle=0x18 | cut -c 34-35";
        String result = LinuxCommand.executeCommand(command, this.getClass().getSimpleName());
        return Integer.parseInt(result, 16);
    }

    private String[] tempAndHumidity() {
        String command = "data=$(gatttool -b " + bluetoothAddress + " --char-write-req --handle=0x10 -n 0100 --listen | grep \"Notification handle\" -m 1) && " +
                "temp=$(echo $data | cut -c 42-54 | xxd -r -p) && " +
                "humidity=$(echo $data | cut -c 64-74 | xxd -r -p) && " +
                "echo $temp-$humidity";

        String result = LinuxCommand.executeCommand(command, this.getClass().getSimpleName());
        return  result.split("-");
    }
}
