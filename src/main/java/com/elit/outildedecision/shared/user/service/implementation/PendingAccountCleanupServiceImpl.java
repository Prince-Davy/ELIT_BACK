package com.elit.outildedecision.shared.user.service.implementation;

import com.elit.outildedecision.shared.auth.repository.VerificationTokenRepository;
import com.elit.outildedecision.shared.user.repository.UserRepository;
import com.elit.outildedecision.shared.user.service.PendingAccountCleanupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Purges accounts that were registered but never confirmed within the token validity window.
 *
 * <p>Tokens are deleted before users to honour the {@code verification_token → user} foreign key.
 * The schedule is driven by the {@code user.cleanup.cron} property and can be disabled by setting it
 * to {@code "-"}.
 */
@Service
@Slf4j
public class PendingAccountCleanupServiceImpl implements PendingAccountCleanupService {

  /**
   * Repository used to select and delete the expired pending accounts.
   */
  private final UserRepository userRepository;

  /**
   * Repository used to delete the verification tokens of the purged accounts.
   */
  private final VerificationTokenRepository verificationTokenRepository;

  /**
   * Instantiates the cleanup service.
   *
   * @param userRepository              the user repository
   * @param verificationTokenRepository the verification token repository
   */
  public PendingAccountCleanupServiceImpl(final UserRepository userRepository,
                                          final VerificationTokenRepository verificationTokenRepository) {
    this.userRepository = userRepository;
    this.verificationTokenRepository = verificationTokenRepository;
  }

  /**
   * {@inheritDoc}
   *
   * <p>Scheduled daily at 03:00 by default; overridable via {@code user.cleanup.cron}.
   */
  @Override
  @Scheduled(cron = "${user.cleanup.cron:0 0 3 * * *}")
  @Transactional
  public int purgeExpiredPendingAccounts() {
    List<Long> expiredIds = userRepository.findExpiredPendingAccountIds(new Date());
    if (expiredIds.isEmpty()) {
      return 0;
    }
    verificationTokenRepository.deleteAllByUserIdIn(expiredIds);
    userRepository.deleteAllByUserIdIn(expiredIds);
    log.info("Purged {} expired pending account(s)", expiredIds.size());
    return expiredIds.size();
  }
}
