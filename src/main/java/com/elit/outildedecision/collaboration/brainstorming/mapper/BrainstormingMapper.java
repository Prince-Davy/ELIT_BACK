package com.elit.outildedecision.collaboration.brainstorming.mapper;

import com.elit.outildedecision.collaboration.brainstorming.dto.BrainstormingRequest;
import com.elit.outildedecision.collaboration.brainstorming.dto.BrainstormingResponse;
import com.elit.outildedecision.collaboration.brainstorming.entity.Brainstorming;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper for the collaborative brainstorming feature.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface BrainstormingMapper {

  /**
   * Maps an inbound brainstorming request to a new entity. The owning user, question and
   * predictibility analysis are resolved and set by the service inside the transaction.
   *
   * @param request the inbound payload
   * @return the brainstorming entity
   */
  @Mapping(target = "brainstormingId", ignore = true)
  @Mapping(target = "formQuestion", ignore = true)
  @Mapping(target = "predictibilityAnalysis", ignore = true)
  @Mapping(target = "user", ignore = true)
  Brainstorming toEntity(BrainstormingRequest request);

  /**
   * Maps a persisted brainstorming to its outbound representation, enriched with the owning
   * question id and the author's project role resolved by the service.
   *
   * @param brainstorming the brainstorming entity
   * @param questionId the owning question id
   * @param role the author's role on the project
   * @return the response DTO
   */
  @Mapping(target = "questionId", source = "questionId")
  @Mapping(target = "role", source = "role")
  @Mapping(target = "predictibilityAnalysisId", source = "brainstorming.predictibilityAnalysis.predictibilityAnalysisId")
  @Mapping(target = "userId", source = "brainstorming.user.userId")
  @Mapping(target = "firstName", source = "brainstorming.user.userFirstName")
  @Mapping(target = "lastName", source = "brainstorming.user.userLastName")
  BrainstormingResponse toResponse(Brainstorming brainstorming, Long questionId, String role);
}
