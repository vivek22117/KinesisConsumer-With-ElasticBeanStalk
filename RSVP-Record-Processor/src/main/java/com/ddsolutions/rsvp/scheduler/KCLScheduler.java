package com.ddsolutions.rsvp.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import software.amazon.kinesis.coordinator.Scheduler;

@Component
public class KCLScheduler implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(KCLScheduler.class);
    private Scheduler scheduler;

    @Autowired
    public KCLScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public void run(String... args) {
        LOGGER.info("Starting kinesis scheduler........");
        Thread schedulerThread = new Thread(scheduler);
        schedulerThread.setDaemon(true);
        LOGGER.info("Thread created..... {}", schedulerThread.getName());

        schedulerThread.start();
    }
}
