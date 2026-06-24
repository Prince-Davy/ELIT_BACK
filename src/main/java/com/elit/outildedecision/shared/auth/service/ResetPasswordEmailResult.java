package com.elit.outildedecision.shared.auth.service;

/**
 * Result of a reset-password e-mail request.
 *
 * @param outcome the business outcome
 * @param detail  the variable part of the response message (the e-mail for {@code ACCOUNT_NOT_FOUND},
 *                the underlying message for {@code NOT_VERIFIED}/{@code INVALID_JSON}); {@code null}
 *                when the outcome needs no detail
 */
public record ResetPasswordEmailResult(ResetEmailOutcome outcome, String detail) {

  /**
   * Builds a result carrying only an outcome (no detail).
   *
   * @param outcome the business outcome
   * @return the result with a {@code null} detail
   */
  public static ResetPasswordEmailResult of(final ResetEmailOutcome outcome) {
    return new ResetPasswordEmailResult(outcome, null);
  }
}
