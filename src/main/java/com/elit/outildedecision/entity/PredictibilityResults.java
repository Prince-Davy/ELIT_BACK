package com.elit.outildedecision.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name = "predictibility_result")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PredictibilityResults {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long predictibilityResultsId;

  @ManyToOne()
  @JoinColumn(
      name = "predictibilityAnalysisId",
      referencedColumnName = "predictibility_analysis_id")
  @JsonBackReference("predictibility_analysis_results")
  private PredictibilityAnalysis predictibilityAnalysis;

  private String name;
  private Float value;
}
