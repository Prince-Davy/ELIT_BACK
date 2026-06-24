package com.elit.outildedecision.shared.auth.service.implementation;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.elit.outildedecision.shared.auth.exception.InvalidJwtException;
import com.elit.outildedecision.shared.auth.service.AuthenticatedToken;
import com.elit.outildedecision.shared.auth.service.JwtService;
import com.elit.outildedecision.shared.config.JwtProperties;
import com.elit.outildedecision.shared.security.AppUserPrincipal;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Default {@link JwtService} backed by the Auth0 {@code java-jwt} library.
 *
 * <p>Sole place in the codebase that builds, signs and verifies JWTs: it owns the signing algorithm,
 * the claim names and the {@code Bearer} prefix so no duplication leaks into the controller or the
 * security filters.
 */
@Service
public class JwtServiceImpl implements JwtService {

  /**
   * Prefix of the {@code Authorization} header for bearer tokens.
   */
  private static final String BEARER_PREFIX = "Bearer ";

  /**
   * Claim holding the granted roles.
   */
  private static final String CLAIM_ROLES = "roles";

  /**
   * Claim holding the user first name.
   */
  private static final String CLAIM_FIRSTNAME = "firstname";

  /**
   * Claim holding the user last name.
   */
  private static final String CLAIM_LASTNAME = "lastname";

  /**
   * Claim holding the user identifier.
   */
  private static final String CLAIM_ID = "id";

  /**
   * JWT configuration (secret and expiration times).
   */
  private final JwtProperties jwtProperties;

  /**
   * Signing algorithm built once from the configured secret.
   */
  private final Algorithm algorithm;

  /**
   * Builds the service.
   *
   * @param jwtProperties the JWT configuration
   */
  public JwtServiceImpl(final JwtProperties jwtProperties) {
    this.jwtProperties = jwtProperties;
    this.algorithm = Algorithm.HMAC256(jwtProperties.getSecret().getBytes());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String createAccessToken(final AppUserPrincipal principal, final String issuer) {
    List<String> roles = principal.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .toList();
    return JWT.create()
        .withSubject(principal.getUsername())
        .withExpiresAt(expiry(jwtProperties.getAccessTokenExpirationTime()))
        .withIssuer(issuer)
        .withClaim(CLAIM_ROLES, roles)
        .withClaim(CLAIM_FIRSTNAME, principal.getUserFirstName())
        .withClaim(CLAIM_LASTNAME, principal.getUserLastName())
        .withClaim(CLAIM_ID, principal.getUser().getUserId())
        .sign(algorithm);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String createRefreshToken(final AppUserPrincipal principal, final String issuer) {
    return JWT.create()
        .withSubject(principal.getUsername())
        .withExpiresAt(expiry(jwtProperties.getRefreshTokenExpirationTime()))
        .withIssuer(issuer)
        .sign(algorithm);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Optional<String> resolveToken(final String authorizationHeader) {
    if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
      return Optional.empty();
    }
    return Optional.of(authorizationHeader.substring(BEARER_PREFIX.length()));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public AuthenticatedToken verify(final String token) {
    try {
      DecodedJWT decodedJWT = JWT.require(algorithm).build().verify(token);
      String[] roles = decodedJWT.getClaim(CLAIM_ROLES).asArray(String.class);
      List<String> roleList = roles == null ? List.of() : Arrays.asList(roles);
      return new AuthenticatedToken(decodedJWT.getSubject(), roleList);
    } catch (JWTVerificationException e) {
      throw new InvalidJwtException("Invalid token", e);
    }
  }

  /**
   * Computes an expiry date from now plus the given duration.
   *
   * @param durationMs the validity duration in milliseconds
   * @return the expiry date
   */
  private Date expiry(final long durationMs) {
    return new Date(System.currentTimeMillis() + durationMs);
  }
}
