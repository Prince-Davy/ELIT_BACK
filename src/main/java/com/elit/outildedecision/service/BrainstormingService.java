package com.elit.outildedecision.service;

import com.elit.outildedecision.exception.BrainstormingNotFoundException;
import com.elit.outildedecision.model.BrainstormingModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

public interface BrainstormingService {
  ResponseEntity<String> createBrainstorming(BrainstormingModel brainstormingModel)
      throws Exception;

  ResponseEntity<String> updateBrainstorming(BrainstormingModel brainstormingModel)
      throws Exception;

  ResponseEntity<String> deleteBrainstorming(Long brainstormingId)
      throws BrainstormingNotFoundException;

  ResponseEntity<String> validateOrRejectBrainstorming(
      Long brainstormingId, HttpEntity<String> isChecked)
      throws JsonProcessingException, BrainstormingNotFoundException;

  ResponseEntity<Object> getBrainstorming(Long predictibilityAnalysisId) throws Exception;

  ResponseEntity<Object> getBrainstormingAbstract(Long predictibilityAnalysisId) throws Exception;
}
