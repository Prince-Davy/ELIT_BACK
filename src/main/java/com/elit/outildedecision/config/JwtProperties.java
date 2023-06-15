package com.elit.outildedecision.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
  /**
   * The Secret.
   * The secret key used to sign the JWT token.
   */
  private String secret;
  /**
   * The Expitation time.
   * The expiration duration for JWT token.
   */
  private Long expirationTime;

}
