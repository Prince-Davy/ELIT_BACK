package com.elit.outildedecision;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("non-existing")
@TestPropertySource(locations = "/application-test.properties")
class OutildedecisionApplicationTests {
    @Test
	void contextLoads() {
	}
}
