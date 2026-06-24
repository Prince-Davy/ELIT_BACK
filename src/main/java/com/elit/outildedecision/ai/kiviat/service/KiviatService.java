package com.elit.outildedecision.ai.kiviat.service;

import com.elit.outildedecision.ai.kiviat.dto.KiviatResponse;

/**
 * Retrieves the Kiviat radar values for a project, either from the AI methodology recommendation
 * or from the project's own AI answers.
 */
public interface KiviatService {

  /**
   * Gives the methodology Kiviat reference values for a project.
   *
   * @param projectId the project id
   * @return the Kiviat values matching the AI recommended methodology
   */
  KiviatResponse getKiviatMethodologyValues(Long projectId);

  /**
   * Gives the project Kiviat values computed from the project AI answers.
   *
   * @param projectId the project id
   * @return the Kiviat values averaged per axis
   */
  KiviatResponse getKiviatProjetValues(Long projectId);
}
