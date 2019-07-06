package com.ddsolutions.rsvp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/rsvp")
public class HealthController {


    @GetMapping()
    public void testHealth(){

    }
}
