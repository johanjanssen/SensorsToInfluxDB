package com.example.configuration;

import com.example.sensors.RetrieveAndSendSensorData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;

@Configuration
@Profile("!integration-test")
public class ScheduleConfiguration {
    private static Logger logger = LoggerFactory.getLogger(ScheduleConfiguration.class);

    @Autowired
    private RetrieveAndSendSensorData retrieveAndSendSensorData;

    @Value("${schedule.rate}")
    private String scheduleRate;

    @PostConstruct
    public void logScheduleRate() {
        logger.info("Retrieving sensor data is scheduled every (milliseconds): " + scheduleRate);
    }

    // Possible to override the rate on the commandline: java -jar *.jar --schedule.rate=[]
    @Scheduled(fixedRateString = "${schedule.rate}")
    public void schedule() {
        retrieveAndSendSensorData.start();
    }
}
