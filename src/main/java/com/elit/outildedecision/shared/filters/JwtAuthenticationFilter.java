package com.elit.outildedecision.shared.filters;

import com.elit.outildedecision.shared.auth.service.JwtService;
import com.elit.outildedecision.shared.kernel.entity.User;
import com.elit.outildedecision.shared.security.AppUserPrincipal;
import com.elit.outildedecision.shared.user.service.implementation.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * JwtAuthenticationFilter to configuration jwt.
 */
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
  private static final String USERNAME_KEY = "username";

  private final AuthenticationManager authenticationManager;

  private final UserServiceImpl customUserDetailsService;

  private final JwtService jwtService;

  /**
   * Constructor of JwtAuthenticationFilter.
   *
   * @param authenticationManager    the authentication manager
   * @param customUserDetailsService custom service
   * @param jwtService               the JWT service issuing the tokens
   */
  public JwtAuthenticationFilter(final AuthenticationManager authenticationManager,
      final UserServiceImpl customUserDetailsService,
      final JwtService jwtService) {
    this.authenticationManager = authenticationManager;
    this.customUserDetailsService = customUserDetailsService;
    this.jwtService = jwtService;
  }

  /**
   * This method is called when a POST request is made to /login
   * We get the username and password from the request body
   * and then we use the authentication manager to authenticate the user
   * If the authentication is successful, we will generate a JWT token
   * and send it back to the client.
   * @param request  the request
   * @param response the response
   * @return Authentication the authentication object that contains the
   *         authenticated user details
   * @throws AuthenticationException if the authentication fails for any reason.
   */
  @Override
  public Authentication attemptAuthentication(final HttpServletRequest request,
      final HttpServletResponse response) throws AuthenticationException {
    try {
      // Single Responsibility - Parse request credentials
      Map<String, String> requestMap = new ObjectMapper().readValue(request.getInputStream(), Map.class);

      // Open/Closed Principle - Support multiple username formats
      String username = extractUsernameFromRequest(requestMap);
      String password = requestMap.get("password");

      // Interface Segregation - Store username for error handling (FIXED)
      request.setAttribute(USERNAME_KEY, username); // Store the actual username value

      // Dependency Inversion - Use service abstraction
      AppUserPrincipal appUserPrincipal = new AppUserPrincipal(customUserDetailsService.findByEmail(username));

      UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
          appUserPrincipal, password);

      return authenticationManager.authenticate(authenticationToken);

    } catch (IOException e) {
      throw new AuthenticationServiceException(e.getMessage(), e);
    }
  }

  /**
   * This method is called when the authentication is successful
   * We generate a JWT token and send it back to the client.
   *
   * @param request    the request
   * @param response   the response
   * @param chain      the filter chain
   * @param authResult the authentication object that contains the authenticated
   * user details
   * @throws IOException if there is an error while writing the token to the
   *                     response body
   */
  @Override
  protected void successfulAuthentication(final HttpServletRequest request,
      final HttpServletResponse response,
      final FilterChain chain,
      final Authentication authResult) throws IOException {
    AppUserPrincipal user = (AppUserPrincipal) authResult.getPrincipal(); // get the authenticated user
    String issuer = request.getRequestURL().toString();
    Map<String, String> tokens = new HashMap<>();
    tokens.put("access-token", jwtService.createAccessToken(user, issuer));
    tokens.put("refresh-token", jwtService.createRefreshToken(user, issuer));
    new ObjectMapper()
        .writeValue(response.getOutputStream(), tokens); // send the token as JSON in the response body
  }

  /**
   * This method is called when the authentication is unsuccessful.
   * We send an error message to the client.
   *
   * @param request  the request
   * @param response the response
   * @param failed   the exception that caused the authentication to fail
   * @throws IOException if there is an error while writing the error message to
   *                     the response body
   */
  @Override
  protected void unsuccessfulAuthentication(final HttpServletRequest request,
      final HttpServletResponse response,
      final AuthenticationException failed) throws IOException {

    // Parse the request to get username and password
    String username = (String) request.getAttribute(USERNAME_KEY);

    // Find user by username/email
    User user = customUserDetailsService.findByEmail(username);

    // Check if the account is activated
    if (user != null && !Optional.ofNullable(user.getIsUserActive()).orElse(false)) {
      // Handle the case where the account is not activated
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.getWriter().write("Account is not activated");
    } else if (user == null) {
      // Handle other authentication failures
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.getWriter().write("Authentication failed: " + failed.getMessage());
    }
  }

  /**
   * Extract username from request following Open/Closed Principle.
   * @param  requestMap requestMap for saving data
   * @return extract username from request
   * Supports both 'username' and 'userEmail' fields.
   */
  private String extractUsernameFromRequest(final Map<String, String> requestMap) {
    // Liskov Substitution Principle - Either field can substitute the other
    return Optional.ofNullable(requestMap.get("userEmail"))
        .orElse(requestMap.get(USERNAME_KEY));
  }
}
