package com.elit.outildedecision;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

//@SpringBootTest
//@ActiveProfiles("non-existing")
//@TestPropertySource(locations = "/application-test.properties")
@DataJpaTest
class OutildedecisionApplicationTests {
    @Test
	void contextLoads() {
	}
}