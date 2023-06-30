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
   * The secret key used to sign the JWT token.
   */
  private String secret;
  /**
   * The access token expiration time..
   */
  private Long accessTokenExpirationTime;
  /**
   * The refresh token expiration time..
   */
  private Long refreshTokenExpirationTime;
}
