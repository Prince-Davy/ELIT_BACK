package com.elit.outildedecision.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;


@Entity
@Table(name = "predictibility_results")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
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

  /**
   * get name.
   * @return name
   */
  public String getName() {
    return name;
  }

  /**
   * set name.
   * @param name name
   */
  public void setName(final String name) {
    this.name = name;
  }
}
