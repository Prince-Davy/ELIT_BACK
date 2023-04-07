package com.elit.outildedecision.util;

public enum MethodologyTypes {
  AGILE("agile"),
  HYBRID("hybrid"),
  CLASSIC("classique");

  private final String methodologyTypesName;

  /**
   * Constructor for creating a new MethodologyTypes instance.
   *
   * @param reportTypeName The name of the report type.
   */
  MethodologyTypes(final String reportTypeName) {
    this.methodologyTypesName = reportTypeName;
  }

  @Override
  public String toString() {
    return methodologyTypesName;
  }
}
