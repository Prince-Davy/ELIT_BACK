package com.elit.outildedecision.service;

import com.elit.outildedecision.entity.AnswerIa;
import com.elit.outildedecision.entity.ResultAi;
import com.elit.outildedecision.model.RecommandationItemModel;
import com.elit.outildedecision.model.RecommandationKiviatModel;
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
  ResponseEntity<List<RecommandationItemModel>> getMappings(List<AnswerIa> answers, ResultAi resultAi);

  /**
   * Returns the Kiviat values project depending on metholodogy choice.
   *
   * @param resultAi the result returned by PMM
   * @return RecommandationKiviatModel for projet value methodology
   */
  ResponseEntity<RecommandationKiviatModel> getKiviatProjectValues(ResultAi resultAi);
}
