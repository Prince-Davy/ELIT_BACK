package com.elit.outildedecision.ai.result.mapper;

import com.elit.outildedecision.ai.result.dto.ResultResponse;
import com.elit.outildedecision.ai.result.entity.ResultAi;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper converting AI result entities to their outbound DTO.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ResultMapper {

  /**
   * Maps a stored AI result to its response DTO. The exposed {@code projectId} is the owning
   * project identifier (not the result identifier), preserving the legacy contract.
   *
   * @param resultAi the persisted AI result
   * @return the response DTO
   */
  @Mapping(target = "projectId", source = "project.projectId")
  ResultResponse toResponse(ResultAi resultAi);
}
