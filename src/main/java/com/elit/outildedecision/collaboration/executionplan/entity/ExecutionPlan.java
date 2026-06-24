package com.elit.outildedecision.collaboration.executionplan.entity;

import com.elit.outildedecision.collaboration.predictibility.entity.PredictibilityAnalysis;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/** Execution plan class. */
@Entity
@Table(name = "execution_plan")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "executionPlanId")
public class ExecutionPlan implements Serializable {

  private static final long serialVersionUID = 1L;
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
