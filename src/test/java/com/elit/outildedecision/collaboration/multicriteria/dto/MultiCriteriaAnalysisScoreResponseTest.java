package com.elit.outildedecision.collaboration.multicriteria.dto;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MultiCriteriaAnalysisScoreResponseTest {

  @Test
  void getMultiCriteriaStepScoresTest() {
    MultiCriteriaStepScoreResponse stepScore = new MultiCriteriaStepScoreResponse();
    List<MultiCriteriaStepScoreResponse> list = new ArrayList<>();
    list.add(stepScore);
    MultiCriteriaAnalysisScoreResponse response = new MultiCriteriaAnalysisScoreResponse();
    response.setMultiCriteriaStepScores(list);
    assertEquals(response.getMultiCriteriaStepScores().get(0), stepScore);
  }
}
