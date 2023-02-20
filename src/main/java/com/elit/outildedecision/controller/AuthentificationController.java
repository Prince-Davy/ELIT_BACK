package com.elit.outildedecision.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.elit.outildedecision.entity.User;
import com.elit.outildedecision.exception.CouldNotSaveUserException;
import com.elit.outildedecision.model.UserModel;
import com.elit.outildedecision.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
public class AuthentificationController {
  private static final long EXPIRATION_TIME = 5 * 60 * 1000;

  @Value("${jwt.secret}")
  private static String jwtSecret;

  private final UserService userService;

  public AuthentificationController(UserService userService) {
    this.userService = userService;
  }

  /**
   * @param user
   * @return ResponseEntity<String>
   * @throws CouldNotSaveUserException
   */
  @PostMapping("/register")
  public ResponseEntity<String> register(@RequestBody @Valid UserModel user)
      throws CouldNotSaveUserException {
    return userService.saveUser(user);
  }

  @GetMapping("/refreshToken")
  public void refreshToken(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    String jwtAuthorizationToken = request.getHeader("Authorization");
    if (jwtAuthorizationToken != null && jwtAuthorizationToken.startsWith("Bearer ")) {
      try {

        String refresh_token = jwtAuthorizationToken.substring("Bearer ".length());
        Algorithm algorithm = Algorithm.HMAC256(jwtSecret.getBytes());
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(refresh_token);
        String userEmail = decodedJWT.getSubject();
        User user = userService.findByEmail(userEmail);
        List<String> roles = new ArrayList<>();
        roles.add(user.getIsAdmin() ? "ADMIN" : "USER");
        String access_token =
            JWT.create()
                .withSubject(user.getUserEmail())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles", roles)
                .withClaim("firstname", user.getUserFirstName())
                .withClaim("lastname", user.getUserLastName())
                .withClaim("id", user.getUserId())
                .sign(algorithm);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("access-token", access_token);
        tokens.put("refresh-token", refresh_token);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), tokens);
      } catch (Exception e) {
        response.setHeader("error", e.getMessage());
        response.setStatus(FORBIDDEN.value());
        Map<String, String> error_message = new HashMap<>();
        error_message.put("error-message", e.getMessage());
        response.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), error_message);
      }
    } else {
      throw new RuntimeException("Refresh token is missing");
    }
  }
}
