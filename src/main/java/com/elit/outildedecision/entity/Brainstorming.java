package com.elit.outildedecision.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "brainstorming")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Brainstorming implements Serializable {
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "questionId", referencedColumnName = "questionId")
  @JsonBackReference("question_brainstorming")
  FormQuestion formQuestion;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long brainstormingId;

  private String brainstormingText;
  private Boolean isChecked;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "predictibilityAnalysisId",
      referencedColumnName = "predictibility_analysis_id")
  @JsonBackReference("predictibility_analysis_brainstorming")
  private PredictibilityAnalysis predictibilityAnalysis;

  @ManyToOne
  @JoinColumn(name = "userId", referencedColumnName = "userId")
  @JsonBackReference("user_brainstorming")
  private User user;

  /**
   * @return String
   */
  @Override
  public String toString() {
    return "Brainstorming{"
        + "brainstormingId="
        + brainstormingId
        + ", brainstormingText='"
        + brainstormingText
        + '\''
        + ", isChecked='"
        + isChecked
        + '\''
        + '}';
  }
}
