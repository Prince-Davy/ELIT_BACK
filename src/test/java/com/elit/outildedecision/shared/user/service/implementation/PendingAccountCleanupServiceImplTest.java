package com.elit.outildedecision.shared.user.service.implementation;

import static org.assertj.core.api.Assertions.assertThat;

import com.elit.outildedecision.shared.auth.entity.VerificationToken;
import com.elit.outildedecision.shared.auth.repository.VerificationTokenRepository;
import com.elit.outildedecision.shared.kernel.entity.User;
import com.elit.outildedecision.shared.user.repository.UserRepository;
import com.elit.outildedecision.shared.user.service.PendingAccountCleanupService;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 * Verifies that {@link PendingAccountCleanupService} only purges pending accounts whose confirmation
 * window has fully elapsed, leaving active accounts and still-pending-but-valid accounts untouched.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PendingAccountCleanupServiceImplTest {

  private static final long ONE_DAY_MS = 24L * 60 * 60 * 1000;

  @Autowired
  private PendingAccountCleanupService cleanupService;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private VerificationTokenRepository verificationTokenRepository;

  @Test
  void shouldPurgeOnlyPendingAccountsWithFullyExpiredConfirmationWindow() {
    User active = userRepository.save(buildUser("active@elit.com", true, true));
    User pendingExpired = userRepository.save(buildUser("expired@elit.com", false, false));
    User pendingValid = userRepository.save(buildUser("valid@elit.com", false, false));

    String expiredToken = saveConfirmToken(pendingExpired, new Date(System.currentTimeMillis() - ONE_DAY_MS));
    String validToken = saveConfirmToken(pendingValid, new Date(System.currentTimeMillis() + ONE_DAY_MS));

    int purged = cleanupService.purgeExpiredPendingAccounts();

    assertThat(purged).isEqualTo(1);
    assertThat(userRepository.existsById(pendingExpired.getUserId())).isFalse();
    assertThat(verificationTokenRepository.existsById(expiredToken)).isFalse();
    assertThat(userRepository.existsById(active.getUserId())).isTrue();
    assertThat(userRepository.existsById(pendingValid.getUserId())).isTrue();
    assertThat(verificationTokenRepository.existsById(validToken)).isTrue();
  }

  @Test
  void shouldReturnZeroWhenNoPendingAccountHasExpired() {
    User pendingValid = userRepository.save(buildUser("valid@elit.com", false, false));
    saveConfirmToken(pendingValid, new Date(System.currentTimeMillis() + ONE_DAY_MS));

    int purged = cleanupService.purgeExpiredPendingAccounts();

    assertThat(purged).isZero();
    assertThat(userRepository.existsById(pendingValid.getUserId())).isTrue();
  }

  private User buildUser(final String email, final boolean active, final boolean verified) {
    return User.builder()
        .username(email)
        .userFirstName("First")
        .userLastName("Last")
        .userEmail(email)
        .userPassword("encoded")
        .isUserActive(active)
        .isEmailVerified(verified)
        .isAdmin(false)
        .build();
  }

  private String saveConfirmToken(final User user, final Date expiryDate) {
    VerificationToken token = new VerificationToken();
    token.setToken("token-" + user.getUserId());
    token.setUser(user);
    token.setType("confirm-account");
    token.setExpiryDate(expiryDate);
    verificationTokenRepository.save(token);
    return token.getToken();
  }
}
