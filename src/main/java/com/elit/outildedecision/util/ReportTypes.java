
package com.elit.outildedecision.util;

public enum ReportTypes {
  MULTI_CRITERIA_ANALYSIS("multicriteria_report"),
  PREDICTIBILITY_ANALYSIS("predictibility_report"),
  EXECUTION_PLAN("execution_report");

  private final String reportTypeName;
  /**
   * Constructor for creating a new ReportTypes instance.
   *
   * @param reportTypeName The name of the report type.
   */
  ReportTypes(final String reportTypeName) {
    this.reportTypeName = reportTypeName;
  }

  @Override
  public String toString() {
    return reportTypeName;
  }
}
