
package com.elit.outildedecision;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.repository.init.ResourceReader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;


@SpringBootApplication
public class OutildedecisionApplication {
  /**
   * JdbcTemplate instance for database operations.
   */
  private static JdbcTemplate jdbcTemplate;
  /**
   * LOGGER instance for loggin.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(OutildedecisionApplication.class);
  /**
   * DatabaseInitializer Constucteur.
   * @param jdbcTemplate table name
   */
  public OutildedecisionApplication(final JdbcTemplate jdbcTemplate) {
    OutildedecisionApplication.jdbcTemplate = jdbcTemplate;
  }

  /**
   * This method is used to start the application.
   * @param args the arguments passed to the application.
   */
  public static void main(final String[] args) throws FileNotFoundException {
    SpringApplication.run(OutildedecisionApplication.class, args);
    extracted("user");
    extracted("methodology");
    extracted("method");
    extracted("execution_plan_question");
    extracted("survey_question");
  }

  /**
   * Initialize Insert data.
   * @throws FileNotFoundException if file not found
   * @param table table name
   */
  private static void extracted(final String table) throws FileNotFoundException {
    int count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + table, Integer.class);
    LOGGER.info("Start Count " + table + " table " + count);
    if (count == 0) {
      StringBuilder sqlContent = new StringBuilder();
      try (BufferedReader reader = new BufferedReader(
        new InputStreamReader(Objects.requireNonNull(ResourceReader.class.getClassLoader().getResourceAsStream("liquibase/dev/sql/" + table  +  ".sql"))))) {
        String line;
        while ((line = reader.readLine()) != null) {
          LOGGER.info(line);
          sqlContent.append(line).append("\n");
        }
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      String sql = sqlContent.toString();
      jdbcTemplate.execute(sql);
    }
  }

  /**
   * This method is used to create a bean of type {@link PasswordEncoder}.
   * @return a bean of type {@link PasswordEncoder}.
   */
 @Bean
 public PasswordEncoder passwordEncoder() {
   return new BCryptPasswordEncoder();
 }
}
