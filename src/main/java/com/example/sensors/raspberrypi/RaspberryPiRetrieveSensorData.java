package com.example.sensors.raspberrypi;

import com.example.baseclasses.RetrieveSensorData;
import com.example.util.Rounding;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

@Component
public class RaspberryPiRetrieveSensorData implements RetrieveSensorData {

	public RaspberryPiSensorData retrieve() {
		String cpuTemperatureFile = "/sys/class/thermal/thermal_zone0/temp";
		Path cpuTemperatureFilePath = Paths.get(cpuTemperatureFile);
		Stream<String> lines;
		Optional<String> firstLine = null;
		try {
			lines = Files.lines(cpuTemperatureFilePath);
			firstLine = lines.findFirst();
			lines.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		double cpuTemperature = Double.valueOf(firstLine.get()) / 1000.0;
		double cpuTemperatureRounded = Rounding.round(cpuTemperature, 1);
		return new RaspberryPiSensorData(cpuTemperatureRounded);
	}
}
