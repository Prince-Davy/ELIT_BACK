
package com.elit.outildedecision.shared.auth.controller;

import com.elit.outildedecision.shared.auth.exception.InvalidJwtException;
import com.elit.outildedecision.shared.auth.service.AuthenticatedToken;
import com.elit.outildedecision.shared.auth.service.JwtService;
import com.elit.outildedecision.shared.kernel.entity.User;
import com.elit.outildedecision.shared.exception.CouldNotMailUserException;
import com.elit.outildedecision.shared.user.exception.CouldNotSaveUserException;
import com.elit.outildedecision.shared.exception.RefreshTokenException;
import com.elit.outildedecision.shared.user.exception.UserAlreadyExistsException;
import com.elit.outildedecision.shared.auth.dto.UserRequest;
import com.elit.outildedecision.shared.security.AppUserPrincipal;
import com.elit.outildedecision.shared.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.HttpStatus.FORBIDDEN;

/** Authentification controller class. */
@RestController
@RequestMapping("/")
@Tag(name = "Shared - Authentication", description = "Authentification et gestion des tokens")
public class AuthentificationController {

  /**
   * The JWT service used to verify and issue tokens.
   */
  private final JwtService jwtService;
  /**
   * The User service.
   * The service used to manage the user.
   */
  private final UserService userService;

  /**
   * Instantiates a new Authentification controller.
   * dependency injection via constructor of the user service
   *
   * @param jwtService  the JWT service used to verify the refresh token and issue an access token.
   * @param userService userService an instance of the UserService class, used
   *                    to interact with the user data store.
   */
  public AuthentificationController(final JwtService jwtService, final UserService userService) {
    this.jwtService = jwtService;
    this.userService = userService;
  }

  /**
   * Register a new user.
   *
   * @param user the user to register
   * @return ResponseEntity<String>
   * @throws CouldNotSaveUserException  the exception thrown when the user cannot
   *                                    be saved
   * @throws UserAlreadyExistsException the exception thrown when the user already
   *                                    exist
   */
  @PostMapping("/register")
  public ResponseEntity<String> register(@RequestBody @Valid final UserRequest user)
      throws CouldNotSaveUserException, UserAlreadyExistsException {
    return ResponseEntity.status(HttpStatus.CREATED).body(userService.saveUser(user));
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
    Optional<String> refreshTokenOpt = jwtService.resolveToken(request.getHeader("Authorization"));
    if (refreshTokenOpt.isEmpty()) {
      throw new RefreshTokenException("Refresh token is missing");
    }
    String refreshToken = refreshTokenOpt.get();
    try {
      AuthenticatedToken verified = jwtService.verify(refreshToken);
      User user = userService.findByEmail(verified.subject());
      if (user == null) {
        throw new InvalidJwtException("Unknown user for refresh token");
      }
      String accessToken =
          jwtService.createAccessToken(new AppUserPrincipal(user), request.getRequestURL().toString());

      Map<String, String> tokens = new HashMap<>();
      tokens.put("access-token", accessToken);
      tokens.put("refresh-token", refreshToken);
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      new ObjectMapper().writeValue(response.getOutputStream(), tokens);
    } catch (InvalidJwtException e) {
      response.setHeader("error", e.getMessage());
      response.setStatus(FORBIDDEN.value());
      Map<String, String> errorMessage = new HashMap<>();
      errorMessage.put("error-message", e.getMessage());
      response.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
      new ObjectMapper().writeValue(response.getOutputStream(), errorMessage);
    }
  }

  /**
   * Send a new mail for user.
   *
   * @param user the user to register
   * @return ResponseEntity<String>
   * @throws CouldNotSaveUserException  the exception thrown when the user cannot
   *                                    be saved
   * @throws UserAlreadyExistsException the exception thrown when the user already
   *                                    exist
   */
  @PostMapping("/sendRegistrationConfirmationEmailAgain")
  public ResponseEntity<String> sendRegistrationConfirmationEmailAgain(@RequestBody @Valid final UserRequest user)
      throws CouldNotMailUserException, UserAlreadyExistsException {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(userService.sendRegistrationConfirmationEmailAgain(user));
  }
}
