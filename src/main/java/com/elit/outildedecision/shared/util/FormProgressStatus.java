package com.elit.outildedecision.shared.util;

/**
 * Enumeration representing the different progress statuses of a form.
 */
// S115: "None" is part of the frozen JSON contract (serialized as-is in the project responses'
// formProgressStatus field), so it cannot be renamed to UPPER_SNAKE_CASE without breaking the client.
@SuppressWarnings("java:S115")
public enum FormProgressStatus {
  /**
   * No progress.
   */
  None,

  /**
   * Progress in AI mode (Artificial Intelligence).
   */
  IA,

  /**
   * Progress in collaborative mode.
   */
  COLLABORATIVE,

  /**
   * Progress in both AI and collaborative modes.
   */
  BOTH
}
