
package com.elit.outildedecision.util;

public enum ProjectConfirmationState {
  WAITING("WAITING"),
  CONFIRMED("CONFIRMED"),
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

  @Override
  public String toString() {

    return stateName;
  }
}
