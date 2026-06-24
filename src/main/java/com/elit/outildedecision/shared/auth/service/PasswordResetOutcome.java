package com.elit.outildedecision.shared.auth.service;

/**
 * Business outcome of a password-reset (reinstate) attempt.
 */
public enum PasswordResetOutcome {
  /** The password was updated. */
  UPDATED,
  /** The token is missing or not a password-reset token. */
  INVALID_LINK,
  /** The token has expired. */
  EXPIRED_LINK,
  /** The new password and its confirmation do not match. */
  PASSWORD_MISMATCH
}
