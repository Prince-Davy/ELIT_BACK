
package com.elit.outildedecision.security;

import com.elit.outildedecision.filters.JwtAuthenticationFilter;
import com.elit.outildedecision.filters.JwtAuthorizationFilter;
import com.elit.outildedecision.service.implementation.UserServiceImpl;
import com.google.common.collect.ImmutableList;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Configuration class for Spring Security.
 */
@Configuration
@EnableWebSecurity
public class SpringConfig extends WebSecurityConfigurerAdapter {
  /**
   * The custom user details service used for authentication.
   */
  private final UserServiceImpl customUserDetailsService;

  /**
   * Constructor for SpringConfig.
   * @param customUserDetailsService the custom user details service used for authentication.
   */
  public SpringConfig(final UserServiceImpl customUserDetailsService) {
    this.customUserDetailsService = customUserDetailsService;
  }

  /**
   * Configures the authentication manager with the custom user details service.
   * @param auth the AuthenticationManagerBuilder to configure.
   * @throws Exception if there is an error configuring the authentication manager.
   */
  @Override
  protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(customUserDetailsService);
  }

  /**
   * Configures the HttpSecurity for the application.
   * @param http the HttpSecurity object to configure.
   * @throws Exception if there is an error configuring the HttpSecurity object.
   */
  @Override
  protected void configure(final HttpSecurity http) throws Exception {
    http.csrf().disable();
    http.headers().frameOptions().disable(); //TODO : Remove this line in production
    http.cors().configurationSource(corsConfigurationSource());
    http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    // http.authorizeRequests().antMatchers("/reset-password").permitAll();
    // http.authorizeRequests().antMatchers("/confirm-account").permitAll();
    // http.authorizeRequests().antMatchers("/login").permitAll();
    // http.authorizeRequests().antMatchers("/register").permitAll();
    // http.authorizeRequests().antMatchers("/refreshToken/**").permitAll();
    // http.authorizeRequests().antMatchers("/mywebsockets/**").permitAll();
    http.authorizeRequests().anyRequest().permitAll();
    http.addFilter(new JwtAuthenticationFilter(authenticationManagerBean()));
    http.addFilterBefore(new JwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
    // http.authorizeRequests().antMatchers("/**").permitAll();
  }

  /**
   * Overrides the default authentication manager bean to allow for dependency injection.
   * @return the AuthenticationManager bean.
   * @throws Exception if there is an error creating the AuthenticationManager.
   */
  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  /**
   * Creates and configures the CORS configuration for the application.
   * @return the CorsConfigurationSource object with the configured CORS settings.
   */
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    final CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOriginPatterns(List.of("http://localhost:3000"));
    configuration.setAllowedMethods(
      List.of("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
    // setAllowCredentials(true) is important, otherwise
    // The value of the 'Access-Control-Allow-Origin' header in the response must
    // not be the wildcard '*' when the request's credentials mode is 'include'.
    configuration.setAllowCredentials(true);
    // setAllowedHeaders is important! Without it, OPTIONS preflight request
    // will fail with 403 Invalid CORS request
    configuration.setAllowedHeaders(
      List.of(
        "Authorization",
        "Content-Type",
        "X-Requested-With",
        "accept",
        "Origin",
        "Access-Control-Request-Method",
        "Access-Control-Request-Headers",
        "Access-Control-Allow-Origin"));
    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
