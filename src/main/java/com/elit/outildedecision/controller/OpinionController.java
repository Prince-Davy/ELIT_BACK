
package com.elit.outildedecision.controller;

import com.elit.outildedecision.exception.OpinionAlreadyExistException;
import com.elit.outildedecision.exception.UserNotFoundException;
import com.elit.outildedecision.model.OpinionModel;
import com.elit.outildedecision.service.OpinionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Opinion controller.
 */
@RestController
@RequestMapping("/")
public class OpinionController {
  /**
   * Opinion Service.
   */
  private final OpinionService opinionService;
  /**
   * Constructor for OpinionController.
   * dependency injection for OpinionService
   * @param opinionService OpinionService
   */
  public OpinionController(final OpinionService opinionService) {
    this.opinionService = opinionService;
  }

  /**
   * @param opinionModel OpinionModel
   * @return ResponseEntity<String>
   * @throws UserNotFoundException UserNotFoundException
   * @throws OpinionAlreadyExistException OpinionAlreadyExistException
   */
  @PostMapping("/opinion")
  public ResponseEntity<String> createOpinion(@RequestBody final OpinionModel opinionModel)
      throws UserNotFoundException, OpinionAlreadyExistException {
    return opinionService.createOpinion(opinionModel);
  }

  /**
   * check if opinion is already exist.
   * @return Boolean
   */
  @GetMapping("/opinion/user")
  public Boolean getOpinionIsAlreadyExist() {
    return opinionService.getOpinionIsAlreadyExist();
  }
}
