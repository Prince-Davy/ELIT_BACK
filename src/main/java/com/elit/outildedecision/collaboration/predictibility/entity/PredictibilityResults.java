package com.elit.outildedecision.collaboration.predictibility.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;


/** Predictibility results class. */
@Entity
@Table(name = "predictibility_results")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(of = "predictibilityResultsId")
public class PredictibilityResults implements Serializable {
  private static final long serialVersionUID = 1L;
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
