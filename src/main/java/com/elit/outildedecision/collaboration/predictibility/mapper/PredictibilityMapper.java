package com.elit.outildedecision.collaboration.predictibility.mapper;

import com.elit.outildedecision.collaboration.predictibility.dto.PredictibilityScoreResponse;
import com.elit.outildedecision.collaboration.predictibility.entity.PredictibilityAnalysis;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper between predictibility entities and their response DTOs.
 *
 * <p>Only the scalar fields carried by the entity are mapped here; the aggregated values
 * (average eScore, per-methodology scores and form steps) are computed in the service and
 * set on the returned DTO.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PredictibilityMapper {

  /**
   * Maps a {@link PredictibilityAnalysis} to its score response, copying the iteration and the
   * chosen methodology. Computed fields are left untouched (filled by the service).
   *
   * @param analysis the predictibility analysis entity
   * @return the score response with its scalar fields populated
   */
  @Mapping(target = "predictibilityIteration", source = "predictibilityAnalysisIteration")
  @Mapping(target = "average", ignore = true)
  @Mapping(target = "methodologies", ignore = true)
  @Mapping(target = "formSteps", ignore = true)
  @Mapping(target = "tieBreakRequired", ignore = true)
  PredictibilityScoreResponse toScoreResponse(PredictibilityAnalysis analysis);
}
