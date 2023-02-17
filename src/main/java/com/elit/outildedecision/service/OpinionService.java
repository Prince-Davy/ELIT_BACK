package com.elit.outildedecision.service;

import com.elit.outildedecision.exception.OpinionAlreadyExistException;
import com.elit.outildedecision.exception.UserNotFoundException;
import com.elit.outildedecision.model.OpinionModel;
import org.springframework.http.ResponseEntity;

public interface OpinionService {

  ResponseEntity<String> createOpinion(OpinionModel opinionModel)
      throws UserNotFoundException, OpinionAlreadyExistException;

  Boolean getOpinionIsAlreadyExist();
}
