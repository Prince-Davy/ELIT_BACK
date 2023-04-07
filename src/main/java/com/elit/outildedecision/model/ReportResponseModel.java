package com.elit.outildedecision.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * This class represents the response model for a report.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportResponseModel {

  /**
   * The ID of the report.
   */
  private Long id;

  /**
   * The title of the report.
   */
  private String title;

  /**
   * The type of the report.
   */
  private String type;

  /**
   * The iteration of the report.
   */
  private Integer iteration;

  /**
   * The creation date of the report.
   */
  private Date creationDate;
}
