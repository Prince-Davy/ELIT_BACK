package com.elit.outildedecision.enumeration;

import com.elit.outildedecision.exception.UnsupportedMethodologyTypeException;

import java.util.regex.Pattern;

public enum MethodologyType {
  /**
   * Agile methodology.
   */
  AGILE("agile", Pattern.compile("agile", Pattern.CASE_INSENSITIVE)),
  /**
   * Hybrid methodology.
   */
  HYBRID("hybrid", Pattern.compile("hybride?", Pattern.CASE_INSENSITIVE)),
  /**
   * Classic methodology.
   */
  CLASSIC("classique", Pattern.compile("(classi(c|que)|Traditionnelle)", Pattern.CASE_INSENSITIVE));

  private final String methodologyTypesName;
  private final Pattern methodologyTypesPattern;

  /**
   * Constructor for creating a new MethodologyTypes instance.
   *
   * @param reportTypeName          The name of the report type.
   * @param methodologyTypesPattern the pattern of acceptable sequences for easier
   *                                finding
   */
  MethodologyType(final String reportTypeName, final Pattern methodologyTypesPattern) {
    this.methodologyTypesName = reportTypeName;
    this.methodologyTypesPattern = methodologyTypesPattern;
  }

  @Override
  public String toString() {
    return methodologyTypesName;
  }

  /**
   * Finds a methodology given a loosely matching string.
   * @param methodolody the loosely matching methodology we want to look for.
   * @return the exact methodology
   * @throws UnsupportedMethodologyTypeException if the given string does not
   *                                             match with defined methodologies
   */
  public static MethodologyType find(final String methodolody) throws UnsupportedMethodologyTypeException {
    if (methodolody == null) {
      throw new UnsupportedMethodologyTypeException("Cannot convert null methodolody");
    }

    for (final MethodologyType methodologyType : values()) {
      if (methodologyType.getMethodologyTypesPattern().matcher(methodolody).matches()) {
        return methodologyType;
      }
    }
    throw new UnsupportedMethodologyTypeException("The methodology " + methodolody
        + " cannot be converted to known methodologies. Please contact your local administrator");
  }

  private Pattern getMethodologyTypesPattern() {
    return methodologyTypesPattern;
  }
}
