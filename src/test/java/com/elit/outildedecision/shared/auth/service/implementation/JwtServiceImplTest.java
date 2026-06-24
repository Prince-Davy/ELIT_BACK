package com.elit.outildedecision.shared.auth.service.implementation;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.elit.outildedecision.shared.auth.exception.InvalidJwtException;
import com.elit.outildedecision.shared.auth.service.AuthenticatedToken;
import com.elit.outildedecision.shared.config.JwtProperties;
import com.elit.outildedecision.shared.kernel.entity.User;
import com.elit.outildedecision.shared.security.AppUserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class JwtServiceImplTest {

  private static final String ISSUER = "http://localhost/login";

  private JwtServiceImpl jwtService;
  private AppUserPrincipal principal;

  @BeforeEach
  void setUp() {
    JwtProperties properties = new JwtProperties();
    properties.setSecret("elit-test-jwt-secret-do-not-use-in-production-0123456789");
    properties.setAccessTokenExpirationTime(900000L);
    properties.setRefreshTokenExpirationTime(43200000L);
    jwtService = new JwtServiceImpl(properties);

    User user = new User();
    user.setUserId(42L);
    user.setUserEmail("alice@example.com");
    user.setUserFirstName("Alice");
    user.setUserLastName("Martin");
    user.setIsAdmin(false);
    principal = new AppUserPrincipal(user);
  }

  @Test
  void shouldCreateAccessTokenWithIdentityClaims() {
    // when
    String token = jwtService.createAccessToken(principal, ISSUER);

    // then
    DecodedJWT decoded = JWT.decode(token);
    assertThat(decoded.getSubject()).isEqualTo("alice@example.com");
    assertThat(decoded.getClaim("roles").asList(String.class)).containsExactly("USER");
    assertThat(decoded.getClaim("firstname").asString()).isEqualTo("Alice");
    assertThat(decoded.getClaim("lastname").asString()).isEqualTo("Martin");
    assertThat(decoded.getClaim("id").asLong()).isEqualTo(42L);
    assertThat(decoded.getExpiresAt()).isInTheFuture();
    assertThat(decoded.getIssuer()).isEqualTo(ISSUER);
  }

  @Test
  void shouldCreateRefreshTokenWithSubjectOnly() {
    // when
    String token = jwtService.createRefreshToken(principal, ISSUER);

    // then
    DecodedJWT decoded = JWT.decode(token);
    assertThat(decoded.getSubject()).isEqualTo("alice@example.com");
    assertThat(decoded.getClaim("roles").isMissing()).isTrue();
  }

  @Test
  void shouldResolveBearerToken() {
    assertThat(jwtService.resolveToken("Bearer abc.def.ghi")).contains("abc.def.ghi");
    assertThat(jwtService.resolveToken(null)).isEmpty();
    assertThat(jwtService.resolveToken("abc.def.ghi")).isEmpty();
  }

  @Test
  void shouldVerifyValidTokenAndReturnSubjectAndRoles() {
    // given
    String token = jwtService.createAccessToken(principal, ISSUER);

    // when
    AuthenticatedToken result = jwtService.verify(token);

    // then
    assertThat(result.subject()).isEqualTo("alice@example.com");
    assertThat(result.roles()).containsExactly("USER");
  }

  @Test
  void shouldThrowInvalidJwtExceptionWhenTokenIsTampered() {
    // given
    String tampered = jwtService.createAccessToken(principal, ISSUER) + "tampered";

    // when / then
    assertThatExceptionOfType(InvalidJwtException.class)
        .isThrownBy(() -> jwtService.verify(tampered));
  }
}
