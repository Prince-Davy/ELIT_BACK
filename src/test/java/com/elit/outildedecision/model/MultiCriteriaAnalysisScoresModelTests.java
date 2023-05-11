package com.elit.outildedecision.model;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MultiCriteriaAnalysisScoresModelTests {
  @Test
  void getMultiCriteriaStepScoresTest(){
    MultiCriteriaStepScoreModel multiCriteriaStepScoreModel = new MultiCriteriaStepScoreModel();
    List<MultiCriteriaStepScoreModel> mcsList = new ArrayList<>();
    mcsList.add(multiCriteriaStepScoreModel);
    MultiCriteriaAnalysisScoreModel mcasm = new MultiCriteriaAnalysisScoreModel();
    mcasm.setMultiCriteriaStepScores(mcsList);
    assertEquals(mcasm.getMultiCriteriaStepScores().get(0),multiCriteriaStepScoreModel);
  }
}
