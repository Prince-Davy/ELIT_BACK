package com.elit.outildedecision.ai.answer.mapper;

import com.elit.outildedecision.ai.answer.dto.AnswerResponse;
import com.elit.outildedecision.ai.answer.entity.AiAnswer;
import org.mapstruct.Mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MapStruct mapper converting AI answer entities to their outbound DTO.
 */
@Mapper(componentModel = "spring")
public interface AnswerMapper {

  /**
   * Flattens a list of AI answer entities into an {@link AnswerResponse} keyed by question.
   * On duplicate questions the last answer wins, preserving the legacy behaviour.
   *
   * @param answers the AI answer entities (may be null)
   * @return the response DTO holding the question/answer map
   */
  default AnswerResponse toResponse(final List<AiAnswer> answers) {
    Map<String, String> data = new HashMap<>();
    if (answers != null) {
      for (AiAnswer answer : answers) {
        data.put(answer.getQuestion(), answer.getAnswer());
      }
    }
    return AnswerResponse.builder().data(data).build();
  }
}
