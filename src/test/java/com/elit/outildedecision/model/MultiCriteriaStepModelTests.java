package com.elit.outildedecision.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MultiCriteriaStepModelTests {

  @Test
  public void toStringTest() {
    MultiCriteriaStepModel mcsm = new MultiCriteriaStepModel();
    mcsm.setStepId(1L);
    mcsm.setStepName("stepName");
    final String mcsmString = "FormStep{" +
      "stepId=" + 1 +
      ", stepName='" + "stepName" + '\'' + '}';
    assertEquals(mcsmString,mcsm.toString());
  }
}
