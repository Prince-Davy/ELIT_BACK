package com.elit.outildedecision.collaboration.multicriteria.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MultiCriteriaStepRequestTest {

  @Test
  void gettersAndSettersTest() {
    MultiCriteriaStepRequest request = new MultiCriteriaStepRequest();
    request.setStepId(1L);
    request.setStepName("stepName");
    assertEquals(1L, request.getStepId());
    assertEquals("stepName", request.getStepName());
  }
}
