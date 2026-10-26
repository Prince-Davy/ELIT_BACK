package com.elit.outildedecision.shared.report.dto;

import com.elit.outildedecision.shared.util.ReportTypes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * Response payload describing a report attached to a project.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportResponse {

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
  private ReportTypes type;

  /**
   * The iteration of the report.
   */
  private Integer iteration;

  /**
   * The creation date of the report.
   */
  private Date creationDate;
}
