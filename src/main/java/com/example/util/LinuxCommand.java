package com.example.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class LinuxCommand {
    private static Logger logger = LoggerFactory.getLogger(LinuxCommand.class);

    public static String executeCommand(String command, String className) {
        // To make pipes work
        String[] cmd = {
                "/bin/bash",
                "-c",
                command
        };

        Process process = null;
        try {
            process = Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if(!process.waitFor(20, TimeUnit.SECONDS)) {
                process.destroy();
                throw new SensorException("Linux command took to long and is destroyed", className);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Scanner errorScanner = new Scanner(process.getErrorStream());
        if (errorScanner != null && errorScanner.hasNext()) {
            throw new SensorException("Error executing Linux command", className, errorScanner.nextLine());
        }

        String result = "";
        Scanner scanner = new Scanner(process.getInputStream());
        if (scanner != null && scanner.hasNext()) {
            result = scanner.nextLine();
        }

        try {
            process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            process.destroy();
            scanner.close();
        }
        return result;
    }
}
