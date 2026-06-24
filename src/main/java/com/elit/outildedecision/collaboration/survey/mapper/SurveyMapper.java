package com.elit.outildedecision.collaboration.survey.mapper;

import com.elit.outildedecision.collaboration.survey.dto.SurveyAnswerResponse;
import com.elit.outildedecision.collaboration.survey.dto.SurveyQuestionResponse;
import com.elit.outildedecision.collaboration.survey.entity.SurveyAnswer;
import com.elit.outildedecision.collaboration.survey.entity.SurveyQuestion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper for the collaborative survey feature.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface SurveyMapper {

  /**
   * Maps a persisted survey question to its outbound representation.
   *
   * @param question the survey question entity
   * @return the response DTO
   */
  SurveyQuestionResponse toQuestionResponse(SurveyQuestion question);

  /**
   * Maps a persisted survey answer to its outbound representation, flattening the owning
   * user and question identifiers.
   *
   * @param answer the survey answer entity
   * @return the response DTO
   */
  @Mapping(target = "userId", source = "user.userId")
  @Mapping(target = "questionId", source = "question.id")
  @Mapping(target = "answer", source = "answerValue")
  SurveyAnswerResponse toAnswerResponse(SurveyAnswer answer);
}
