package com.elit.outildedecision.initializer;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * This class is responsible for initializing the database with default users. (Temporary solution instead of Liquibase)
 * It implements the CommandLineRunner interface, which allows it to run
 */
@Component
public final class DatabaseInitializer implements CommandLineRunner {

  /**
   * JdbcTemplate instance for database operations.
   */
  private final JdbcTemplate jdbcTemplate;

  /**
   * Constructor for DatabaseInitializer.
   * @param jdbcTemplate JdbcTemplate instance for database operations.
   */
  public DatabaseInitializer(final JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public void run(final String... args) throws Exception {
    Object[][] users = {
      {"user@user.com", "user", 0, 1, 1, null, "John", "Doe", "$2a$10$PFYEdpbBR.tTcDO6WjkXdevJ6U8W6eMmdgWMUTsM0wnwlhxROqrkG" },
      {"admin@elite.fr", "admin", 1, 1, 1, null, "Nancy", "Michel", "$2a$10$PFYEdpbBR.tTcDO6WjkXdevJ6U8W6eMmdgWMUTsM0wnwlhxROqrkG" },
      {"contributer@elit.com", "contributer", 0, 1, 1, null, "Charles", "Baker", "$2a$10$PFYEdpbBR.tTcDO6WjkXdevJ6U8W6eMmdgWMUTsM0wnwlhxROqrkG" },
      {"observer@elit.com", "observer", 0, 1, 1, null, "Rochelle", "Wright", "$2a$10$PFYEdpbBR.tTcDO6WjkXdevJ6U8W6eMmdgWMUTsM0wnwlhxROqrkG" }
    };

    String insertSql = "INSERT INTO `user` (`user_email`, `username`, `is_admin`, `is_email_verified`, `is_user_active`, `user_avatar_path`, `user_first_name`, `user_last_name`, `user_password`) "
      +
      "SELECT ?, ?, ?, ?, ?, ?, ?, ?, ? "
      +
      "WHERE NOT EXISTS (SELECT 1 FROM `user` WHERE `user_email` = ? OR `username` = ?)";

    for (Object[] user : users) {
      jdbcTemplate.update(insertSql, user[0], user[1], user[2], user[3], user[4], user[5], user[6], user[7], user[8], user[0], user[1]);
    }
  }
}
