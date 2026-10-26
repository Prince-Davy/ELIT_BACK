package com.elit.outildedecision.collaboration.report.entity;

import org.junit.jupiter.api.Test;

import com.elit.outildedecision.shared.util.ReportTypes;


import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReportTest {
  @Test
  void testReportType() {
    Report report = new Report();

    report.setType(ReportTypes.AI_METHODOLOGY);

    assertEquals(ReportTypes.AI_METHODOLOGY, report.getType());
    assertEquals("ai_report_methodology", report.getType().toString());
  }
}
