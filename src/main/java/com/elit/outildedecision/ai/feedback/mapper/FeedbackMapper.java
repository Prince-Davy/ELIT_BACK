package com.elit.outildedecision.ai.feedback.mapper;

import com.elit.outildedecision.ai.feedback.dto.FeedbackMethodResponse;
import com.elit.outildedecision.ai.feedback.dto.FeedbackRequest;
import com.elit.outildedecision.ai.feedback.dto.FeedbackResponse;
import com.elit.outildedecision.ai.feedback.entity.FeedbackIa;
import com.elit.outildedecision.ai.feedback.entity.FeedbackIaMethod;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * MapStruct mapper for the AI feedback feature (methodology feedback and method
 * feedback).
 */
@Mapper(componentModel = "spring")
public interface FeedbackMapper {

  /**
   * Maps an inbound methodology feedback request to its entity.
   *
   * @param request the inbound payload
   * @return the feedback entity
   */
  @Mapping(target = "id", ignore = true)
  FeedbackIa toEntity(FeedbackRequest request);

  /**
   * Maps a methodology feedback entity to its outbound representation.
   *
   * @param feedback the feedback entity
   * @return the response DTO
   */
  @Mapping(target = "projectId", source = "project.projectId")
  @Mapping(target = "userId", source = "user.userId")
  FeedbackResponse toResponse(FeedbackIa feedback);

  /**
   * Maps a method feedback entity to its outbound representation.
   *
   * @param feedback the method feedback entity
   * @return the response DTO
   */
  @Mapping(target = "projectId", source = "project.projectId")
  @Mapping(target = "userId", source = "user.userId")
  @Mapping(target = "methodName", source = "method")
  FeedbackMethodResponse toMethodResponse(FeedbackIaMethod feedback);

  /**
   * Maps a list of method feedback entities to their outbound representations.
   *
   * @param feedbacks the method feedback entities
   * @return the response DTOs
   */
  List<FeedbackMethodResponse> toMethodResponseList(List<FeedbackIaMethod> feedbacks);
}
