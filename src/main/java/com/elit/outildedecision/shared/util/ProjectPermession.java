
package com.elit.outildedecision.shared.util;

/** Project permession enum. */
public enum ProjectPermession {
  /** Observer. */
  OBSERVER("Observateur"),
  /** Contributer. */
  CONTRIBUTER("Contributeur"),
  /** Cdp. */
  CDP("Chef de Projet");
  private final String permessionName;
  /**
   * Constructor for creating a new ProjectPermession instance.
   *
   * @param permessionName The name of the permession.
   */
  ProjectPermession(final String permessionName) {
    this.permessionName = permessionName;
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {

    return permessionName;
  }
}
