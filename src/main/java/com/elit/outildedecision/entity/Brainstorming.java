package com.elit.outildedecision.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Entity representing a brainstorming entry linked to a question, user, and predictibility analysis.
 * This class is mapped to the "brainstorming" table in the database.
 */
@Entity
@Table(name = "brainstorming")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Brainstorming implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * The question associated with this brainstorming entry.
   * Lazily loaded to optimize performance.
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "questionId", referencedColumnName = "questionId")
  @JsonBackReference("question_brainstorming")
  private FormQuestion formQuestion;

  /**
   * Unique identifier for the brainstorming entry.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long brainstormingId;

  /**
   * The text content of the brainstorming idea.
   */
  private String brainstormingText;

  /**
   * Indicates whether the brainstorming idea has been marked as checked.
   */
  private Boolean isChecked;

  /**
   * The predictibility analysis associated with this brainstorming entry.
   * Lazily loaded to optimize performance.
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
    name = "predictibilityAnalysisId",
    referencedColumnName = "predictibility_analysis_id"
  )
  @JsonBackReference("predictibility_analysis_brainstorming")
  private PredictibilityAnalysis predictibilityAnalysis;

  /**
   * The user who created the brainstorming entry.
   */
  @ManyToOne
  @JoinColumn(name = "userId", referencedColumnName = "userId")
  @JsonBackReference("user_brainstorming")
  private User user;

  /**
   * Returns a string representation of the brainstorming entry.
   *
   * @return a string containing the ID, text, and checked status
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
