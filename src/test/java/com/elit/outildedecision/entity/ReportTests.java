package com.elit.outildedecision.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReportTests {
  @Test
  void testToString(){

   Report report = new Report();
   report.setType("pdf");
   final String typeString = "pdf";
    assertEquals(typeString, report.getType());
  }
}
