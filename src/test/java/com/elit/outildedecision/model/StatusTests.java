package com.elit.outildedecision.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class StatusTests {
  @Test
  public void enumTests(){
    assertNotNull(Status.JOIN);
    assertNotNull(Status.MESSAGE);
    assertNotNull(Status.LEAVE);
  }
}
