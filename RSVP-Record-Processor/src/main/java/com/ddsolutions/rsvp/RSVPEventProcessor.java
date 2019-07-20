package com.ddsolutions.rsvp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:application.properties")
public class RSVPEventProcessor {

    public static void main(String[] args) {
        SpringApplication.run(RSVPEventProcessor.class, args);
    }
}
