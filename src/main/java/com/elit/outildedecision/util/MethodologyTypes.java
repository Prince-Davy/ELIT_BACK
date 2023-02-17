package com.elit.outildedecision.util;

public enum MethodologyTypes {
  AGILE("agile"),
  HYBRID("hybrid"),
  CLASSIC("classique");

  private final String MethodologyTypesName;

  MethodologyTypes(String reportTypeName) {
    this.MethodologyTypesName = reportTypeName;
  }

  @Override
  public String toString() {
    return MethodologyTypesName;
  }
}
