package com.elit.outildedecision.service;

import com.elit.outildedecision.ai.answer.entity.AiAnswer;
import com.elit.outildedecision.entity.ResultAi;
import com.elit.outildedecision.model.RecommandationItemModel;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * This interface allows for retrival of Json recommandations based on AI surey's answers.
 */
public interface RecommandationService {

  /**
   * Returns the mappings depending on answers.
   *
   * @param answers  the answers submitted by user to AI survey
   * @param resultAi the result returned by PMM
   * @return a list of recommandation items
   */
  ResponseEntity<List<RecommandationItemModel>> getMappings(List<AiAnswer> answers, ResultAi resultAi);
}
