package com.elit.outildedecision.collaboration.predictibility.entity;

import com.elit.outildedecision.collaboration.brainstorming.entity.Brainstorming;
import com.elit.outildedecision.collaboration.executionplan.entity.ExecutionPlan;
import com.elit.outildedecision.collaboration.multicriteria.entity.MultiCriteriaAnalysis;
import com.elit.outildedecision.collaboration.vote.entity.Vote;
import com.elit.outildedecision.shared.methodology.entity.Methodology;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

/** Predictibility analysis class. */
@Entity
@Table(name = "predictibility_analysis")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(of = "predictibilityAnalysisId")
public class PredictibilityAnalysis implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "predictibility_analysis_id")
  private Long predictibilityAnalysisId;

  private Integer predictibilityAnalysisIteration;
  private Boolean isFinished;
  private String chosenMethodology;

  @CreationTimestamp
  @JsonFormat(pattern = "dd-MM-yyyy", timezone = "Europe/Paris")
  private Timestamp createdAt;

  @UpdateTimestamp
  @JsonFormat(pattern = "dd-MM-yyyy", timezone = "Europe/Paris")
  private Timestamp updatedAt;

  @OneToOne(mappedBy = "predictibilityAnalysis", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonBackReference("execution_previsibility")
  private ExecutionPlan executionPlan;

  @OneToMany(mappedBy = "predictibilityAnalysis", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JsonManagedReference("predictibility_analysis_vote")
  private List<Vote> votes;

  @OneToMany(
    mappedBy = "predictibilityAnalysis",
    cascade = CascadeType.ALL,
    fetch = FetchType.LAZY,
    orphanRemoval = true)
  @JsonManagedReference("predictibility_analysis_brainstorming")
  private List<Brainstorming> brainstormings;

  @OneToOne(
    cascade = CascadeType.ALL
    // orphanRemoval = true
  )
  @JoinColumn(name = "multi_criteria_analysis_id", referencedColumnName = "multiCriteriaAnalysisId")
  @JsonManagedReference("previsibility_multi")
  private MultiCriteriaAnalysis multiCriteriaAnalysis;

  @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
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
