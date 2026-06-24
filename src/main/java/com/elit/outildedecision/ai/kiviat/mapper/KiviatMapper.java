package com.elit.outildedecision.ai.kiviat.mapper;

import com.elit.outildedecision.ai.kiviat.dto.KiviatResponse;
import com.elit.outildedecision.ai.kiviat.dto.KiviatScores;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * Maps the internal {@link KiviatScores} computation carrier to the wire {@link KiviatResponse}.
 * Axis names match one-to-one, so no explicit field mapping is required.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface KiviatMapper {

  /**
   * Converts computed Kiviat scores into the response DTO returned to the front.
   *
   * @param scores the computed axis scores
   * @return the wire response DTO
   */
  KiviatResponse toResponse(KiviatScores scores);
}
