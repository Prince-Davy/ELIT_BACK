package com.elit.outildedecision.collaboration.vote.entity;

import com.elit.outildedecision.collaboration.predictibility.entity.PredictibilityAnalysis;
import com.elit.outildedecision.shared.kernel.entity.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import java.io.Serializable;
import java.util.Date;

/** Vote class. */
@Entity
@Table(name = "vote")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Vote implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "vote_id", nullable = false, updatable = false)
  @EqualsAndHashCode.Include
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

  @ManyToOne(optional = false)
  @JoinColumn(name = "userId")
  // Serialised only via VoteSummaryResponse (UserSummaryResponse), never as a raw entity.
  private User user;

  /**
   * @return String representation of Vote.
   */
  @Override
  public String toString() {
    return "predictibilityValue:" + predictibilityValue;
  }
}
