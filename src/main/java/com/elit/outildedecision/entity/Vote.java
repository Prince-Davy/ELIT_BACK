package com.elit.outildedecision.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vote implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
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
   * @return String
   */
  @Override
  public String toString() {
    return "predictibilityValue:" + predictibilityValue;
  }
}
