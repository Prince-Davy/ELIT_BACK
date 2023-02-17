package com.elit.outildedecision.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(exclude = "multiCriteriaAnalysis")
public class PredictibilityAnalysis implements Serializable {
  @OneToMany(mappedBy = "predictibilityAnalysis", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JsonManagedReference("predictibility_analysis_vote")
  List<Vote> votes;

  @OneToMany(
      mappedBy = "predictibilityAnalysis",
      cascade = CascadeType.ALL,
      fetch = FetchType.LAZY,
      orphanRemoval = true)
  @JsonManagedReference("predictibility_analysis_brainstorming")
  List<Brainstorming> brainstormings;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "predictibility_analysis_id")
  private Long predictibilityAnalysisId;

  private Integer predictibilityAnalysisIteration;
  private Boolean isFinished;

  @CreationTimestamp
  @JsonFormat(pattern = "dd-MM-yyyy", timezone = "Europe/Paris")
  private Timestamp createdAt;

  @UpdateTimestamp
  @JsonFormat(pattern = "dd-MM-yyyy", timezone = "Europe/Paris")
  private Timestamp updatedAt;

  @OneToOne(mappedBy = "predictibilityAnalysis", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonBackReference("execution_previsibility")
  private ExecutionPlan executionPlan;

  @OneToOne(
      cascade = CascadeType.ALL
      // orphanRemoval = true
      )
  @JoinColumn(name = "multi_criteria_analysis_id", referencedColumnName = "multiCriteriaAnalysisId")
  @JsonManagedReference("previsibility_multi")
  private MultiCriteriaAnalysis multiCriteriaAnalysis;

  @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JoinColumn(referencedColumnName = "methodologyId", name = "methodology_id")
  private Methodology methodology;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "predictibilityAnalysis")
  @JsonManagedReference("predictibility_analysis_results")
  private List<PredictibilityResults> predictibilityResults;

  /**
   * @return String
   */
  @Override
  public String toString() {
    return "PredictibilityAnalysis{"
        + "predictibilityAnalysisId="
        + predictibilityAnalysisId
        + ", predictibilityAnalysisIteration='"
        + predictibilityAnalysisIteration
        + '\''
        + ", isFinished='"
        + isFinished
        + '\''
        + '}';
  }
}
