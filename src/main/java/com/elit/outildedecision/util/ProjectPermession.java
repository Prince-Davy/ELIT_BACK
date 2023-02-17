package com.elit.outildedecision.util;

public enum ProjectPermession {
  OBSERVER("Observateur"),
  CONTRIBUTER("Contributeur"),
  CDP("Chef de Projet");
  private final String permessionName;

  ProjectPermession(String permessionName) {
    this.permessionName = permessionName;
  }

  @Override
  public String toString() {

    return permessionName;
  }
}
