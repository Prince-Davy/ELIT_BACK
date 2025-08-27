package com.elit.outildedecision.service;


import com.elit.outildedecision.ai.answer.exception.AiAnswerNotFoundException;
import com.elit.outildedecision.exception.ResultAiNotFoundException;
import com.elit.outildedecision.model.RecommandationKiviatModel;
import org.springframework.http.ResponseEntity;

/**
 * This interface allows for retrival of Json kiviatModel based on AI surey's answers or user answers.
 */
public interface KiviatService {

  /**
   * Give the methodology kiviat values for project.
   *
   * @param projectId the project id
   *
   * @return recommandation kiviat model by methodology
   */
  ResponseEntity<RecommandationKiviatModel> getKiviatMethodologyValues(Long projectId)
    throws ResultAiNotFoundException;

  /**
   * Give the project kiviat values for project with answer from user.
   *
   * @param projectId the project id
   *
   * @return recommandation kiviat model by project
   */
  ResponseEntity<RecommandationKiviatModel> getKiviatProjetValues(Long projectId)
    throws AiAnswerNotFoundException;

}
