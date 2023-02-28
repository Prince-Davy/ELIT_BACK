package com.elit.outildedecision.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "form_step")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(exclude = {"stepWeights", "form"})
public class FormStep {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long stepId;

  private String stepName;
  private Long stepRef;
  private Float eScore;

  @ManyToOne(optional = false)
  @JoinColumn(name = "multi_criteria_analysis_id", referencedColumnName = "multiCriteriaAnalysisId")
  @JsonBackReference("multiCriteria_step")
  private MultiCriteriaAnalysis multiCriteriaAnalysis;

  @OneToMany(mappedBy = "formStep", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("question_step")
  private Set<FormQuestion> formQuestions;

  @OneToMany(
      mappedBy = "formStep",
      fetch = FetchType.EAGER,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  @JsonManagedReference("step_weight")
  private Set<StepWeight> stepWeights;

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
