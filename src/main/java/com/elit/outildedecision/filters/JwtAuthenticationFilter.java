package com.elit.outildedecision.filters;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.elit.outildedecision.config.JwtProperties;
import com.elit.outildedecision.security.AppUserPrincipal;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
  private final JwtProperties jwtProperties;
  private final AuthenticationManager authenticationManager;
  /**
   * Constructor of JwtAuthenticationFilter.
   * @param jwtProperties
   * @param authenticationManager the authentication manager
   */
  public JwtAuthenticationFilter(final JwtProperties jwtProperties, final AuthenticationManager authenticationManager) {
    this.jwtProperties = jwtProperties;
    this.authenticationManager = authenticationManager;
  }

  /**
   * This method is called when a POST request is made to /login
   * We get the username and password from the request body
   * and then we use the authentication manager to authenticate the user
   * If the authentication is successful, we will generate a JWT token
   * and send it back to the client.
   * @param request the request
   * @param response the response
   * @return Authentication the authentication object that contains the authenticated user details
   * @throws AuthenticationException if the authentication fails for any reason.
   */
  @Override
  public Authentication attemptAuthentication(final HttpServletRequest request,
                                              final HttpServletResponse response) throws AuthenticationException {
    try {
      Map<String, String> requestMap =
          new ObjectMapper().readValue(request.getInputStream(), Map.class);
      String username = requestMap.get("username");
      String password = requestMap.get("password");
      UsernamePasswordAuthenticationToken authenticationToken =
          new UsernamePasswordAuthenticationToken(username, password);
      return authenticationManager.authenticate(authenticationToken);
    } catch (IOException e) {
      throw new AuthenticationServiceException(e.getMessage(), e);
    }
  }
  /**
   * This method is called when the authentication is successful
   * We generate a JWT token and send it back to the client.
   * @param request the request
   * @param response the response
   * @param chain the filter chain
   * @param authResult the authentication object that contains the authenticated user details
   * @throws IOException if there is an error while writing the token to the response body
   */
  @Override
  protected void successfulAuthentication(final HttpServletRequest request,
                                          final HttpServletResponse response,
                                          final FilterChain chain,
                                          final Authentication authResult) throws IOException {
    AppUserPrincipal user =
        (AppUserPrincipal) authResult.getPrincipal(); // get the authenticated user
    Algorithm algorithm =
        Algorithm.HMAC256(jwtProperties.getSecret().getBytes());
    String accessToken =
        JWT.create()
            .withSubject(user.getUsername()) // the (email) of the authenticated user
            .withExpiresAt(
                new Date(
                    System.currentTimeMillis() + jwtProperties.getAccessTokenExpirationTime())) // the token will expire in 15min
            .withIssuer(
                request
                    .getRequestURL()
                    .toString()) // the issuer of the token is the URL of the server
            .withClaim(
                "roles",
                user.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList())) // the
            // roles
            // of
            // the
            // authenticated
            // user
            .withClaim(
                "firstname", user.getUserFirstName()) // the first name of the authenticated user
            .withClaim(
                "lastname", user.getUserLastName()) // the last name of the authenticated user
            .withClaim("id", user.getUser().getUserId()) // the user id of the authenticated user
            .sign(algorithm); // sign the token using our secret
    String refreshToken =
        JWT.create()
            .withSubject(user.getUsername()) // the username of the authenticated user
            .withExpiresAt(
                new Date(
                    System.currentTimeMillis() + jwtProperties.getRefreshTokenExpirationTime())) // the token will expire
            // in 30 days
            .withIssuer(
                request
                    .getRequestURL()
                    .toString()) // the issuer of the token is the URL of the server
            .sign(algorithm); // sign the token using our secret
    Map<String, String> tokens = new HashMap<>();

    tokens.put("access-token", accessToken);
    tokens.put("refresh-token", refreshToken);
    // response.addHeader("Access-Control-Allow-Origin", "*"); // this one allows to
    // escape the CORS problem
    new ObjectMapper()
        .writeValue(response.getOutputStream(), tokens); // send the token as JSON in the response
    // body
  }

  /**
   * This method is called when the authentication is unsuccessful.
   * We send an error message to the client.
   * @param request the request
   * @param response the response
   * @param failed the exception that caused the authentication to fail
   * @throws IOException if there is an error while writing the error message to the response body
   */
  @Override
  protected void unsuccessfulAuthentication(final HttpServletRequest request,
                                            final HttpServletResponse response,
                                            final AuthenticationException failed) throws IOException {

    Map<String, String> errors = new HashMap<>();
    errors.put("access-token", "access_token");
    errors.put("refresh-token", "refresh_token");

    // escape the CORS problem
    response.sendError(1, "error");
    new ObjectMapper().writeValue(response.getOutputStream(), errors); // send the tok
  }
}
