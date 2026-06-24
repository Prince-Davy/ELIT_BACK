package com.elit.outildedecision.shared.auth.service;

/**
 * Business outcome of a reset-password e-mail request.
 */
public enum ResetEmailOutcome {
  /** The reset e-mail was sent. */
  SENT,
  /** No account matches the supplied e-mail. */
  ACCOUNT_NOT_FOUND,
  /** The account exists but its e-mail is not verified. */
  NOT_VERIFIED,
  /** The request body could not be parsed as JSON. */
  INVALID_JSON,
  /** The reset e-mail could not be delivered. */
  MAIL_FAILED
}
