
package com.elit.outildedecision.util;

public enum ReportTypes {
  /** multicriteria report .*/
  MULTI_CRITERIA_ANALYSIS("multicriteria_report"),
  /** predictibility report .*/
  PREDICTIBILITY_ANALYSIS("predictibility_report"),
  /** execution report . */
  EXECUTION_PLAN("execution_report");

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
  @Override
  public String toString() {
    return reportTypeName;
  }
}
