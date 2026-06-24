package com.elit.outildedecision.shared.user.service;

/**
 * Maintenance service purging accounts that were registered but never confirmed in time.
 */
public interface PendingAccountCleanupService {

  /**
   * Deletes the pending accounts whose confirmation window has fully elapsed, together with their
   * verification tokens.
   *
   * @return the number of purged accounts
   */
  int purgeExpiredPendingAccounts();
}
