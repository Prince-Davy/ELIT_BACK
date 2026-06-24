package com.elit.outildedecision.collaboration.multicriteria.dto;

import com.elit.outildedecision.collaboration.brainstorming.dto.BrainstormingSummaryResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * Outbound representation of a multi-criteria form question with its criteria and brainstormings.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class FormQuestionResponse implements Serializable {

  private static final long serialVersionUID = 1L;

  /** Unique identifier for the question. */
  private Long questionId;

  /** The question text. */
  private String questionText;

  /** The answer text provided for the question. */
  private String answerText;

  /** The functional reference of the question. */
  private String questionRef;

  /** Whether the question has been updated. */
  private Boolean updateQuestion;

  /** The criteria attached to the question. */
  private List<CriteriaResponse> criterias;

  /** The brainstormings attached to the question. */
  private List<BrainstormingSummaryResponse> brainstormings;
}
