package com.elit.outildedecision.collaboration.multicriteria.mapper;

import com.elit.outildedecision.collaboration.brainstorming.dto.BrainstormingSummaryResponse;
import com.elit.outildedecision.collaboration.brainstorming.entity.Brainstorming;
import com.elit.outildedecision.collaboration.multicriteria.dto.FormStepResponse;
import com.elit.outildedecision.collaboration.multicriteria.dto.MulticriteriaAnalysisResponse;
import com.elit.outildedecision.collaboration.multicriteria.entity.FormStep;
import com.elit.outildedecision.collaboration.multicriteria.entity.MultiCriteriaAnalysis;
import com.elit.outildedecision.collaboration.vote.dto.VoteSummaryResponse;
import com.elit.outildedecision.collaboration.vote.entity.Vote;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.Collection;
import java.util.List;

/**
 * MapStruct mapper between multi-criteria analysis entities and their response DTOs. It resolves the
 * lazy entity graph (form steps, questions, criteria, brainstormings, votes) into self-contained
 * DTOs while still inside the calling transaction, so no entity is ever serialised by Jackson.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface MulticriteriaMapper {

  /**
   * Maps a {@link MultiCriteriaAnalysis} entity to its response DTO. The predictibility analysis is
   * a computed/cross-feature field assembled in the service layer, so it is ignored here.
   *
   * @param analysis the multi-criteria analysis entity
   * @return the corresponding response DTO
   */
  @Mapping(target = "predictibilityAnalysis", ignore = true)
  MulticriteriaAnalysisResponse toResponse(MultiCriteriaAnalysis analysis);

  /**
   * Maps the form steps of an analysis to their response DTOs.
   *
   * @param formSteps the form step entities
   * @return the corresponding response DTOs
   */
  List<FormStepResponse> toFormStepResponses(Collection<FormStep> formSteps);

  /**
   * Maps a single form step. The explicit {@code eScore} mapping disambiguates the Lombok
   * single-letter-prefix accessor ({@code getEScore} -> JavaBeans property {@code EScore}).
   *
   * @param formStep the form step entity
   * @return the corresponding response DTO
   */
  @Mapping(target = "eScore", source = "EScore")
  FormStepResponse toFormStepResponse(FormStep formStep);

  /**
   * Maps brainstorming entries to their lightweight response DTOs.
   *
   * @param brainstormings the brainstorming entities
   * @return the corresponding response DTOs
   */
  List<BrainstormingSummaryResponse> toBrainstormingSummaries(Collection<Brainstorming> brainstormings);

  /**
   * Maps votes to their response DTOs (with the voter as a minimal user summary).
   *
   * @param votes the vote entities
   * @return the corresponding response DTOs
   */
  List<VoteSummaryResponse> toVoteSummaries(Collection<Vote> votes);
}
