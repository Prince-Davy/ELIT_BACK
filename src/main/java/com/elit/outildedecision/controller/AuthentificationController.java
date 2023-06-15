
package com.elit.outildedecision.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.elit.outildedecision.config.JwtProperties;
import com.elit.outildedecision.entity.User;
import com.elit.outildedecision.exception.CouldNotSaveUserException;
import com.elit.outildedecision.exception.RefreshTokenException;
import com.elit.outildedecision.exception.UserAlreadyExistsException;
import com.elit.outildedecision.model.UserModel;
import com.elit.outildedecision.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.FORBIDDEN;

@RestController
@RequestMapping("/")
public class AuthentificationController {

  /**
   * The Jwt Properties.
   * The properties used to configure the JWT token.
   */
  private final JwtProperties jwtProperties;
  /**
   * The User service.
   * The service used to manage the user.
   */
  private final UserService userService;

  /**
   * Instantiates a new Authentification controller.
   * dependency injection via constructor of the user service
   *
   * @param jwtProperties the jwt properties used to get the secret key to sign the JWT token.
   * @param userService   userService an instance of the UserService class, used to interact with the user data store.
   */
  public AuthentificationController(final JwtProperties jwtProperties, final UserService userService) {
    this.jwtProperties = jwtProperties;
    this.userService = userService;
  }

  /**
   * Register a new user.
   *
   * @param user the user to register
   * @return ResponseEntity<String>
   * @throws CouldNotSaveUserException the exception thrown when the user cannot be saved
   * @throws UserAlreadyExistsException the exception thrown when the user already exist
   */
  @PostMapping("/register")
  public ResponseEntity<String> register(@RequestBody @Valid final UserModel user)
    throws CouldNotSaveUserException, UserAlreadyExistsException {
    return userService.saveUser(user);
  }

  /**
   * Generate a refresh token and an access token.
   *
   * @param request  the request containing the user credentials
   * @param response the response containing the tokens or the error message
   * @throws IOException the exception thrown when the response cannot be written
   */
  @GetMapping("/refreshToken")
  public void refreshToken(final HttpServletRequest request,
                           final HttpServletResponse response)
    throws IOException, RefreshTokenException {
    String jwtAuthorizationToken = request.getHeader("Authorization");
    if (jwtAuthorizationToken != null && jwtAuthorizationToken.startsWith("Bearer ")) {
      try {
        String refreshToken = jwtAuthorizationToken.substring("Bearer ".length());
        Algorithm algorithm = Algorithm.HMAC256(jwtProperties.getSecret().getBytes());
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(refreshToken);
        String userEmail = decodedJWT.getSubject();
        User user = userService.findByEmail(userEmail);
        List<String> roles = new ArrayList<>();
        roles.add(Boolean.TRUE.equals(user.getIsAdmin()) ? "ADMIN" : "USER");
        String accessToken =
          JWT.create()
            .withSubject(user.getUserEmail())
            .withExpiresAt(new Date(System.currentTimeMillis() + jwtProperties.getExpirationTime()))
            .withIssuer(request.getRequestURL().toString())
            .withClaim("roles", roles)
            .withClaim("firstname", user.getUserFirstName())
            .withClaim("lastname", user.getUserLastName())
            .withClaim("id", user.getUserId())
            .sign(algorithm);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("access-token", accessToken);
        tokens.put("refresh-token", refreshToken);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), tokens);
      } catch (Exception e) {
        response.setHeader("error", e.getMessage());
        response.setStatus(FORBIDDEN.value());
        Map<String, String> errorMessage = new HashMap<>();
        errorMessage.put("error-message", e.getMessage());
        response.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), errorMessage);
      }
    } else {
      throw new RefreshTokenException("Refresh token is missing");
    }
  }
}
