package com.elit.outildedecision.ai.methodology.evaluation.api.mapper;

import com.elit.outildedecision.ai.answer.entity.AiAnswer;
import com.elit.outildedecision.ai.methodology.evaluation.api.dto.ExplanatoryFactorDto;
import com.elit.outildedecision.ai.methodology.evaluation.api.dto.MethodologyEvaluationDto;
import com.elit.outildedecision.ai.methodology.evaluation.entity.MethodologyEvaluation;
import org.springframework.stereotype.Component;

import java.util.List;

/** Methodology evaluation mapper class. */
@Component
public class MethodologyEvaluationMapper {

  /**
   * Mapper entity to DTO that also adds AI answers to each question.
   *
   * @param evaluation evaluation
   * @param aiAnswers  aiAnswers
   * @return dto
   */
  public MethodologyEvaluationDto toDto(final MethodologyEvaluation evaluation, final List<AiAnswer> aiAnswers) {
    var questionScores = evaluation.getQuestionScores();

    var explanatoryFactorsDto = aiAnswers.stream()
      .filter(aiAnswer -> questionScores.containsKey(aiAnswer.getQuestion()))
      .map(aiAnswer -> ExplanatoryFactorDto.builder()
        .question(aiAnswer.getQuestion())
        .questionScore(questionScores.get(aiAnswer.getQuestion()))
        .answer(aiAnswer.getAnswer())
        .build())
      .toList();

    return MethodologyEvaluationDto.builder()
      .methodology(evaluation.getMethodology())
      .score(evaluation.getScore())
      .explanatoryFactorDto(explanatoryFactorsDto)
      .build();
  }
}
