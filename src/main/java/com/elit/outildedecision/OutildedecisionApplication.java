
package com.elit.outildedecision;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@SpringBootApplication
public class OutildedecisionApplication {

  /**
   * This method is used to start the application.
   * @param args the arguments passed to the application.
   */
  public static void main(final String[] args) {
    SpringApplication.run(OutildedecisionApplication.class, args);
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
