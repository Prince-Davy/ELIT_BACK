package com.elit.outildedecision.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "execution_plan")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"predictibilityAnalysis"})
public class ExecutionPlan implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long executionPlanId;

  private Integer executionIteration;

  @CreationTimestamp
  @JsonFormat(pattern = "dd-MM-yyyy", timezone = "Europe/Paris")
  private Date createdAt;

  @UpdateTimestamp
  @JsonFormat(pattern = "dd-MM-yyyy", timezone = "Europe/Paris")
  private Date updatedAt;

  private Boolean isFinished;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(
      name = "predictibility_analysis_id",
      referencedColumnName = "predictibility_analysis_id")
  @JsonManagedReference("execution_previsibility")
  private PredictibilityAnalysis predictibilityAnalysis;

  @OneToMany(mappedBy = "executionPlan", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ExecutionPlanAnswer> executionPlanAnswers;

  /**
   * @return String
   */
  @Override
  public String toString() {
    return "ExecutionPlan{"
        + "executionPlanId="
        + executionPlanId
        + ", createdAt="
        + createdAt
        + ", updatedAt="
        + updatedAt
        + '}';
  }
}
