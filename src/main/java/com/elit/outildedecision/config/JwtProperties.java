package com.elit.outildedecision.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
  /**
   * The Secret.
   * The secret key used to sign the JWT token.
   */
  private String secret;
  /**
   * Gets secret.
   * @return the secret key used to sign the JWT token.
   */
  public String getSecret() {
        return secret;
    }
    /**
     * Sets secret.
     * @param jwtSecret the secret key used to sign the JWT token.
     */
    public void setSecret(final String jwtSecret) {
        this.secret = jwtSecret;
    }
}
