package com.elit.outildedecision.util;

import com.elit.outildedecision.exception.UnsupportedMethodologyTypeException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.regex.Pattern;

/**
 * Enumeration for specific development methodologies.
 * Separates concrete implementation methods from high-level methodology types.
 * Following Single Responsibility Principle.
 */
public enum MethodType {
  /**
   * Scrum methodology.
   */
  SCRUM("scrum", Pattern.compile("scrum", Pattern.CASE_INSENSITIVE)),
  /**
   * Kanban methodology.
   */
  KANBAN("kanban", Pattern.compile("kanban", Pattern.CASE_INSENSITIVE)),
  /**
   * Waterfall methodology.
   */
  WATERFALL("waterfall", Pattern.compile("waterfall", Pattern.CASE_INSENSITIVE));

  private final String methodTypeName;
  private final Pattern methodTypePattern;

  /**
   * Constructor for creating a new MethodType instance.
   *
   * @param methodTypeName    The name of the method type.
   * @param methodTypePattern the pattern of acceptable sequences for easier
   *                          finding
   */
  MethodType(final String methodTypeName, final Pattern methodTypePattern) {
    this.methodTypeName = methodTypeName;
    this.methodTypePattern = methodTypePattern;
  }

  @Override
  @JsonValue
  public String toString() {
    return methodTypeName;
  }

  /**
   * Finds a method type given a loosely matching string.
   * 
   * @param method the loosely matching method we want to look for.
   * @return the exact method type
   * @throws UnsupportedMethodologyTypeException if the given string does not
   *                                             match with defined method types
   */
  @JsonCreator
  public static MethodType find(final String method) throws UnsupportedMethodologyTypeException {
    if (method == null) {
      throw new UnsupportedMethodologyTypeException("Cannot convert null method");
    }

    for (final MethodType methodType : values()) {
      if (methodType.getMethodTypePattern().matcher(method).matches()) {
        return methodType;
      }
    }
    throw new UnsupportedMethodologyTypeException("The method " + method
        + " cannot be converted to known method types. Please contact your local administrator");
  }

  private Pattern getMethodTypePattern() {
    return methodTypePattern;
  }
}
