package com.elit.outildedecision.snapshot;

import java.util.regex.Pattern;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.ValueMatcher;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import org.skyscreamer.jsonassert.comparator.JSONComparator;

/**
 * Centralizes the JSONAssert customizations for volatile fields (plan §10.5).
 *
 * <p>The default comparison is STRICT. Each tolerated field is declared here with a
 * documented reason; adding one requires a justification in review. Keeping this list
 * closed is what makes the snapshots a meaningful contract.
 */
final class SnapshotComparators {

  /** Three base64url segments separated by dots — a signed JWT. */
  private static final Pattern JWT_PATTERN =
      Pattern.compile("^[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+$");

  private static final ValueMatcher<Object> IS_JWT =
      (actual, expected) -> actual != null && JWT_PATTERN.matcher(actual.toString()).matches();

  private static final ValueMatcher<Object> IS_PRESENT =
      (actual, expected) -> actual != null && !actual.toString().isBlank();

  private SnapshotComparators() {
  }

  /**
   * STRICT comparator that only tolerates the login token fields, whose values are
   * non-deterministic by construction (signed with a time-based expiry claim).
   *
   * @return a comparator for {@code POST /login} responses
   */
  static JSONComparator loginTokens() {
    return new CustomComparator(
        org.skyscreamer.jsonassert.JSONCompareMode.STRICT,
        new Customization("access-token", IS_JWT),
        new Customization("refresh-token", IS_JWT));
  }

  /**
   * STRICT comparator tolerating server-generated timestamps that the deterministic seed
   * cannot freeze (e.g. {@code @CreationTimestamp}). Only the listed paths are relaxed.
   *
   * @param timestampPaths the JSON paths whose value is a generated timestamp
   * @return a comparator tolerating exactly those paths
   */
  static JSONComparator tolerating(final String... timestampPaths) {
    Customization[] customizations = new Customization[timestampPaths.length];
    for (int i = 0; i < timestampPaths.length; i++) {
      customizations[i] = new Customization(timestampPaths[i], IS_PRESENT);
    }
    return new CustomComparator(
        org.skyscreamer.jsonassert.JSONCompareMode.STRICT, customizations);
  }
}
