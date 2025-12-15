package com.elit.outildedecision.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "vote")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vote implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "vote_id", nullable = false, updatable = false)
  private Long voteId;

  @CreationTimestamp
  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Europe/Paris")
  private Date createdAt;

  private Integer predictibilityValue;

  private String chosenPMM;

  private String justification;

  private Boolean isConfirmed;

  @ManyToOne(optional = false)
  @JoinColumn(
      name = "predictibilityAnalysisId",
      referencedColumnName = "predictibility_analysis_id")
  @JsonBackReference("predictibility_analysis_vote")
  private PredictibilityAnalysis predictibilityAnalysis;

  @OneToOne
  @JoinColumn(name = "userId", nullable = true)
  // @JsonManagedReference("user_vote")
  private User user;

  /**
   * @return String representation of Vote.
   */
  @Override
  public String toString() {
    return "predictibilityValue:" + predictibilityValue;
  }
}
