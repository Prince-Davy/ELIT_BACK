package com.elit.outildedecision.ai.answer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Map;

/**
 * Inbound payload carrying the AI questionnaire answers as a question/answer map.
 * Consumed by the answer, analysis and result AI endpoints (compat: JSON shape {@code {"data": {...}}}).
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnswerRequest {

  /**
   * Question/answer pairs submitted for the project.
   */
  @NotNull
  private Map<@NotBlank @Size(max = 255) String, @NotBlank @Size(max = 1000) String> data;
}
