package com.elit.outildedecision.collaboration.report.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReportTest {
  @Test
  void testToString(){

   Report report = new Report();
   report.setType("pdf");
   final String typeString = "pdf";
    assertEquals(typeString, report.getType());
  }
}
