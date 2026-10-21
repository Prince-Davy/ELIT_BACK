
package com.elit.outildedecision.shared.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;

/** Report types enum. */
public enum ReportTypes {
  /** multicriteria report .*/
  MULTI_CRITERIA_ANALYSIS("multicriteria_report"),
  /** predictibility report .*/
  PREDICTIBILITY_ANALYSIS("predictibility_report"),
  /** execution report . */
  EXECUTION_PLAN("execution_report"),
  /** AI report. */
  AI_ANALYSIS("ai_report"),
  /** audit report. */
  AUDIT("audit_report");

  /** report name .*/
  private final String reportTypeName;

  /**
   * Constructor for creating a new ReportTypes instance.
   *
   * @param reportTypeName The name of the report type.
   */
  ReportTypes(final String reportTypeName) {
    this.reportTypeName = reportTypeName;
  }

  /**
   * convert to string .
   * @return the reportTypeName
   */
  @JsonValue
  @Override
  public String toString() {
    return reportTypeName;
  }

  /**
   * Get the ReportTypes enum from a string value.
   *
   * @param value The string representation of the report type.
   * @return The corresponding ReportTypes enum.
   * @throws IllegalArgumentException if the value does not match any report type.
   */
  @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
  public static ReportTypes fromValue(final String value) {
    return Arrays.stream(values())
            .filter(type -> type.reportTypeName.equals(value))
            .findFirst()
            .orElseThrow(() ->
                    new IllegalArgumentException(
                            "Type de rapport inconnu : " + value
                    )
            );
  }

  
}
