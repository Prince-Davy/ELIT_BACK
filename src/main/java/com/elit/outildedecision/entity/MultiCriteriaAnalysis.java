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
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "multi_criteria_analysis")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(exclude = "project")
public class MultiCriteriaAnalysis implements Serializable {
  @OneToMany(mappedBy = "multiCriteriaAnalysis", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("multiCriteria_step")
  private List<FormStep> formSteps;

  @ManyToOne(optional = false, cascade = CascadeType.ALL)
  @JoinColumn(name = "project_id", referencedColumnName = "projectId")
  @JsonBackReference("multiCriteria_project")
  private Project project;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long multiCriteriaAnalysisId;

  @CreationTimestamp
  @JsonFormat(pattern = "dd-MM-yyyy", timezone = "Europe/Paris")
  private Timestamp createdAt;

  @UpdateTimestamp
  @JsonFormat(pattern = "dd-MM-yyyy", timezone = "Europe/Paris")
  private Timestamp updatedAt;

  private Integer multiCriteriaAnalysisIteration;
  private Boolean isFinished;

  @OneToOne(mappedBy = "multiCriteriaAnalysis", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonBackReference("previsibility_multi")
  private PredictibilityAnalysis predictibilityAnalysis;

  /**
   * @return String
   */
  @Override
  public String toString() {
    return "MultiCriteriaAnalysis{"
        + "multiCriteriaAnalysisId="
        + multiCriteriaAnalysisId
        + ", createdAt="
        + createdAt
        + ", updatedAt="
        + updatedAt
        + ", multiCriteriaAnalysisIteration="
        + multiCriteriaAnalysisIteration
        + ", isFinished="
        + isFinished
        + ", formSteps="
        + formSteps
        + '}';
  }
}
