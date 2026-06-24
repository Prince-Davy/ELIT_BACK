package com.elit.outildedecision.ai.answer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * Outbound payload returning the stored AI answers as a question/answer map
 * (compat: JSON shape {@code {"data": {...}}}).
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnswerResponse {

  /**
   * Question/answer pairs stored for the project.
   */
  private Map<String, String> data;
}
