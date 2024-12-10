package com.elit.outildedecision.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OpinionTests {
  @Test
  void testToString(){

    Opinion opinion = new Opinion();
    opinion.setDescription("Description");
    final String descriptionString = "Description";
    assertEquals(descriptionString, opinion.getDescription());
  }
}
