package com.elit.outildedecision.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Home.
 */
@CrossOrigin
@RestController
public class ApiHomeController {
    /**
     * Tell us if the server is up.
     *
     * @return "Server is up"
     */
    @GetMapping("/api")
    public String index() {
        return "Api is up";
    }
}