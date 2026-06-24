package com.elit.outildedecision.collaboration.vote.mapper;

import com.elit.outildedecision.collaboration.vote.dto.VoteRequest;
import com.elit.outildedecision.collaboration.vote.dto.VoteResponse;
import com.elit.outildedecision.collaboration.vote.entity.Vote;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper for the collaborative vote feature.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface VoteMapper {

  /**
   * Maps an inbound vote request to a new vote entity. The owning user and predictibility
   * analysis are resolved and set by the service inside the transaction.
   *
   * @param request the inbound payload
   * @return the vote entity
   */
  @Mapping(target = "voteId", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "predictibilityAnalysis", ignore = true)
  @Mapping(target = "user", ignore = true)
  Vote toEntity(VoteRequest request);

  /**
   * Maps a persisted vote to its outbound representation.
   *
   * @param vote the vote entity
   * @return the response DTO
   */
  @Mapping(target = "predictibilityAnalysisId", source = "predictibilityAnalysis.predictibilityAnalysisId")
  @Mapping(target = "userId", source = "user.userId")
  VoteResponse toResponse(Vote vote);
}
