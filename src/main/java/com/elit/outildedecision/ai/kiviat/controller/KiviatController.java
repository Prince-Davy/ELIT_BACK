package com.elit.outildedecision.ai.kiviat.controller;

import com.elit.outildedecision.ai.kiviat.dto.KiviatResponse;
import com.elit.outildedecision.ai.kiviat.service.KiviatService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Exposes the Kiviat radar values for a project (AI parcours). Errors are delegated to the global
 * exception handler.
 */
@Slf4j
@RestController
@RequestMapping("/kiviat")
@Tag(name = "AI - Kiviat", description = "Valeurs du radar Kiviat d'un projet")
public class KiviatController {

  private final KiviatService kiviatService;

  /**
   * Constructor for KiviatController.
   *
   * @param kiviatService the Kiviat service
   */
  public KiviatController(final KiviatService kiviatService) {
    this.kiviatService = kiviatService;
  }

  /**
   * Returns the Kiviat values matching the AI recommended methodology.
   *
   * @param projectId the project id
   * @return the methodology Kiviat values
   */
  @GetMapping("/methodology/{projectId}")
  @PreAuthorize("isProjectOwner(#projectId)")
  public KiviatResponse getKiviatMethodologyValues(@PathVariable final Long projectId) {
    log.info("Kiviat methodology values requested for projectId={}", projectId);
    return kiviatService.getKiviatMethodologyValues(projectId);
  }

  /**
   * Returns the Kiviat values computed from the project AI answers.
   *
   * @param projectId the project id
   * @return the project Kiviat values
   */
  @GetMapping("/project/{projectId}")
  @PreAuthorize("isProjectOwner(#projectId)")
  public KiviatResponse getKiviatProjectValues(@PathVariable final Long projectId) {
    log.info("Kiviat project values requested for projectId={}", projectId);
    return kiviatService.getKiviatProjetValues(projectId);
  }
}
