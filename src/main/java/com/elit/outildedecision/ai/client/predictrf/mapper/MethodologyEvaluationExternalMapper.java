package com.elit.outildedecision.ai.client.predictrf.mapper;

import com.elit.outildedecision.ai.client.predictrf.dto.MethodologyEvaluationExternalDto;
import com.elit.outildedecision.ai.methodology.evaluation.entity.MethodologyEvaluation;
import org.springframework.stereotype.Component;

@Component
public final class MethodologyEvaluationExternalMapper {
  /**
   * mapper to entity.
   *
   * @param dto dto
   * @return entity
   */
  public MethodologyEvaluation toEntity(final MethodologyEvaluationExternalDto dto) {
    return MethodologyEvaluation.builder()
      .score(dto.getPredictedScore())
      .methodology(dto.getPredictedMethodology())
      .questionScores(dto.getFeatureImportante())
      .build();
  }
}
