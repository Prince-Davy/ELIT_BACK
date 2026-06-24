package com.elit.outildedecision.shared.auth.service;

import com.elit.outildedecision.shared.security.AppUserPrincipal;

import java.util.Optional;

/**
 * Single owner of the JWT (JSON Web Token) lifecycle for authentication.
 *
 * <p>Centralises creation, parsing and verification of access/refresh tokens so the controller and
 * the security filters never touch the underlying JWT library directly. Exposes only neutral types
 * (no library class leaks through this contract).
 */
public interface JwtService {

  /**
   * Builds a signed access token carrying the principal identity and roles.
   *
   * @param principal the authenticated principal
   * @param issuer    the token issuer (the request URL)
   * @return the signed access token
   */
  String createAccessToken(AppUserPrincipal principal, String issuer);

  /**
   * Builds a signed refresh token (subject and expiry only).
   *
   * @param principal the authenticated principal
   * @param issuer    the token issuer (the request URL)
   * @return the signed refresh token
   */
  String createRefreshToken(AppUserPrincipal principal, String issuer);

  /**
   * Extracts the raw token from an {@code Authorization} header value.
   *
   * @param authorizationHeader the raw header value (may be {@code null})
   * @return the bearer token if present and well-formed, otherwise empty
   */
  Optional<String> resolveToken(String authorizationHeader);

  /**
   * Verifies a token signature and expiry and returns its identity claims.
   *
   * @param token the raw token to verify
   * @return the verified subject and roles
   * @throws InvalidJwtException if the token is missing, malformed, expired or tampered with
   */
  AuthenticatedToken verify(String token);
}
