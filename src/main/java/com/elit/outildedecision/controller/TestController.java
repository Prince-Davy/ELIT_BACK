package com.elit.outildedecision.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * TestController.
 */
@RestController
public class TestController {
  /**
     * Tell us for test server.
     *
     * @return "Server is up"
     */
  @GetMapping("/test")
  @ResponseStatus(HttpStatus.OK)
  public String test() {
    return "Hello World";
  }
}
