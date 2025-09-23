package com.elit.outildedecision;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class OutildedecisionApplicationTests {
    @Autowired
    private ApplicationContext context;

    @Test
    void contextLoads() {
      Assertions.assertNotNull(context, "The application context should not be null");
    }
}
