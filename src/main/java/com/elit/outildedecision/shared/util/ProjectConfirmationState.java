
package com.elit.outildedecision.shared.util;

/** Project confirmation state enum. */
public enum ProjectConfirmationState {
  /** Waiting. */
  WAITING("WAITING"),
  /** Confirmed. */
  CONFIRMED("CONFIRMED"),
  /** Rejected. */
  REJECTED("REJECTED");

  private final String stateName;

  /**
   * Constructor for creating a new ProjectConfirmationState instance.
   *
   * @param stateName The name of the state.
   */
  ProjectConfirmationState(final String stateName) {
    this.stateName = stateName;
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {

    return stateName;
  }
}
