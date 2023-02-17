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

@RestController
@RequestMapping("/api")
public class OpinionController {
  private final OpinionService opinionService;

  public OpinionController(OpinionService opinionService) {
    this.opinionService = opinionService;
  }

  /**
   * @param opinionModel
   * @return ResponseEntity<String>
   * @throws UserNotFoundException
   * @throws OpinionAlreadyExistException
   */
  @PostMapping("/opinion")
  public ResponseEntity<String> createOpinion(@RequestBody OpinionModel opinionModel)
      throws UserNotFoundException, OpinionAlreadyExistException {
    return opinionService.createOpinion(opinionModel);
  }

  @GetMapping("/opinion/user")
  public Boolean getOpinionIsAlreadyExist() {
    return opinionService.getOpinionIsAlreadyExist();
  }
}
