package com.elit.outildedecision.shared.auth.service;

/**
 * Business outcome of an account-confirmation attempt.
 */
public enum ConfirmationOutcome {
  /** The account was confirmed and activated. */
  CONFIRMED,
  /** The token is missing or not a confirmation token. */
  INVALID_TOKEN,
  /** The token has expired. */
  EXPIRED
}
