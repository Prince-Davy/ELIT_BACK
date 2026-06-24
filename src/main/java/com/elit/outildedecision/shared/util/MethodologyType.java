package com.elit.outildedecision.shared.util;

import com.elit.outildedecision.shared.exception.UnsupportedMethodologyTypeException;

import java.util.regex.Pattern;

/** Methodology type enum. */
public enum MethodologyType {
  /**
   * Agile methodology.
   */
  AGILE("agile", Pattern.compile("agile", Pattern.CASE_INSENSITIVE), "Agile"),
  /**
   * Hybrid methodology.
   */
  HYBRID("hybrid", Pattern.compile("hybride?", Pattern.CASE_INSENSITIVE), "Hybride"),
  /**
   * Classic methodology.
   */
  CLASSIC("classique", Pattern.compile("(classi(c|que)|Traditionnelle)", Pattern.CASE_INSENSITIVE), "Classique");

  private final String methodologyTypesName;
  private final Pattern methodologyTypesPattern;
  private final String frenchLabel;

  /**
   * Constructor for creating a new MethodologyTypes instance.
   *
   * @param reportTypeName          The name of the report type.
   * @param methodologyTypesPattern the pattern of acceptable sequences for easier
   *                                finding
   * @param frenchLabel             the French label persisted as the methodology name in database
   */
  MethodologyType(final String reportTypeName, final Pattern methodologyTypesPattern, final String frenchLabel) {
    this.methodologyTypesName = reportTypeName;
    this.methodologyTypesPattern = methodologyTypesPattern;
    this.frenchLabel = frenchLabel;
  }

  /**
   * Returns the French label used as the methodology name in the database (e.g. {@code Agile},
   * {@code Hybride}, {@code Classique}).
   *
   * @return the French label of the methodology
   */
  public String getFrenchLabel() {
    return frenchLabel;
  }

  /** {@inheritDoc} */
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

  /**
   * get the methodology type .
   * @return the methodology type
   */
  private Pattern getMethodologyTypesPattern() {
    return methodologyTypesPattern;
  }
}
