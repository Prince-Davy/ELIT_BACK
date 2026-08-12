package com.elit.outildedecision.collaboration.multicriteria.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

/** Form step class. */
@Entity
@Table(name = "form_step")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "stepId")
public class FormStep implements Serializable {

  /** Serialization version identifier. */
  private static final long serialVersionUID = 1L;
  /** The step id. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long stepId;

  /** The step name. */
  private String stepName;
  /** The step ref. */
  private Long stepRef;
  /** The e score. */
  private Float eScore;

  /** The multi criteria analysis. */
  @ManyToOne(optional = false)
  @JoinColumn(name = "multi_criteria_analysis_id", referencedColumnName = "multiCriteriaAnalysisId")
  @JsonBackReference("multiCriteria_step")
  private MultiCriteriaAnalysis multiCriteriaAnalysis;

  /** The form questions. */
  @OneToMany(mappedBy = "formStep", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("question_step")
  private Set<FormQuestion> formQuestions;

  /** The step weights. */
  @OneToMany(
      mappedBy = "formStep",
      fetch = FetchType.EAGER,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  @JsonManagedReference("step_weight")
  private Set<StepWeight> stepWeights;

  /** The step scores. */
  @OneToMany(
      mappedBy = "formStep",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.EAGER)
  @JsonManagedReference("step_score")
  private List<StepScore> stepScores;

  /**
   * @return String
   */
  @Override
  public String toString() {
    return "FormStep{" + "stepId=" + stepId + ", stepName='" + stepName + '\'' + '}';
  }
}
