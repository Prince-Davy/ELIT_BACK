package com.elit.outildedecision.ai.result.service;

import com.elit.outildedecision.ai.answer.entity.AiAnswer;
import com.elit.outildedecision.ai.result.dto.RecommendationItemResponse;
import com.elit.outildedecision.ai.result.entity.ResultAi;

import java.util.List;

/**
 * This interface allows for retrival of Json recommendations based on AI surey's answers.
 */
public interface RecommendationService {

  /**
   * Returns the mappings depending on answers.
   *
   * @param answers  the answers submitted by user to AI survey
   * @param resultAi the result returned by PMM
   * @return a list of recommendation items
   */
  List<RecommendationItemResponse> getMappings(List<AiAnswer> answers, ResultAi resultAi);
}
