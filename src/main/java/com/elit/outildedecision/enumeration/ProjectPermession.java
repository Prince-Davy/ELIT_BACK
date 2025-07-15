
package com.elit.outildedecision.enumeration;

public enum ProjectPermession {
  OBSERVER("Observateur"),
  CONTRIBUTER("Contributeur"),
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

  @Override
  public String toString() {

    return permessionName;
  }
}
