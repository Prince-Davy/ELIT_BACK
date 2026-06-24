package com.elit.outildedecision.collaboration.executionplan.mapper;

import com.elit.outildedecision.collaboration.executionplan.dto.ExecutionPlanFormResponse;
import com.elit.outildedecision.collaboration.executionplan.entity.ExecutionPlanAnswer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * MapStruct mapper between execution plan entities and their response DTOs.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ExecutionPlanMapper {

  /**
   * Maps an {@link ExecutionPlanAnswer} (with its question) to a form entry DTO.
   *
   * @param answer the execution plan answer entity
   * @return the corresponding form response DTO
   */
  @Mapping(target = "title", source = "executionPlanQuestion.title")
  @Mapping(target = "description", source = "executionPlanQuestion.question")
  @Mapping(target = "executionQuestionId", source = "executionPlanQuestion.executionPlanQuestionId")
  ExecutionPlanFormResponse toFormResponse(ExecutionPlanAnswer answer);

  /**
   * Maps a list of {@link ExecutionPlanAnswer} to a list of form entry DTOs.
   *
   * @param answers the execution plan answers
   * @return the corresponding form response DTOs
   */
  List<ExecutionPlanFormResponse> toFormResponses(List<ExecutionPlanAnswer> answers);
}
