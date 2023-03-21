
package com.elit.outildedecision.service;

import com.elit.outildedecision.exception.OpinionAlreadyExistException;
import com.elit.outildedecision.exception.UserNotFoundException;
import com.elit.outildedecision.model.OpinionModel;
import org.springframework.http.ResponseEntity;
/**
 * This interface defines the contract for the Opinion Service, which provides methods to create and retrieve opinions.
 */
public interface OpinionService {

  /**
   * Creates a new opinion.
   *
   * @param opinionModel an OpinionModel object representing the opinion to create
   * @return a ResponseEntity indicating the success or failure of the create operation
   * @throws UserNotFoundException if the specified user does not exist
   * @throws OpinionAlreadyExistException if an opinion from the same user already exists for the same entity
   */
  ResponseEntity<String> createOpinion(OpinionModel opinionModel)
    throws UserNotFoundException, OpinionAlreadyExistException;

  /**
   * Checks if an opinion already exists for the specified entity and user.
   *
   * @return true if an opinion already exists for the specified entity and user, false otherwise
   */
  Boolean getOpinionIsAlreadyExist();
}
