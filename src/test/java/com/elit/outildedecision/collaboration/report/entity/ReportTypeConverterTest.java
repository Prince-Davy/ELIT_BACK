package com.elit.outildedecision.collaboration.report.entity;

import com.elit.outildedecision.shared.util.ReportTypeConverter;
import com.elit.outildedecision.shared.util.ReportTypes;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Database and JSON contracts must use stable historic type strings. */
class ReportTypeConverterTest {

  private final ReportTypeConverter converter = new ReportTypeConverter();

  @Test
  void preservesLegacyDatabaseValues() {
    for (ReportTypes type : ReportTypes.values()) {
      assertEquals(type, converter.convertToEntityAttribute(converter.convertToDatabaseColumn(type)));
    }
    assertEquals("multicriteria_report",
      converter.convertToDatabaseColumn(ReportTypes.MULTI_CRITERIA_ANALYSIS));
    assertEquals("ai_report_methodology", converter.convertToDatabaseColumn(ReportTypes.AI_METHODOLOGY));
    assertEquals("ai_report_result", converter.convertToDatabaseColumn(ReportTypes.AI_RESULT));
    assertEquals("audit_report", converter.convertToDatabaseColumn(ReportTypes.AUDIT));
    assertEquals(ReportTypes.MULTI_CRITERIA_ANALYSIS,
      converter.convertToEntityAttribute("MULTI_CRITERIA_ANALYSIS"));
    assertNull(converter.convertToEntityAttribute(null));
    assertNull(converter.convertToDatabaseColumn(null));
  }

  @Test
  void serializesWithLegacyHttpValue() throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();
    assertEquals("\"execution_report\"",
      objectMapper.writeValueAsString(ReportTypes.EXECUTION_PLAN));
    assertEquals(ReportTypes.MULTI_CRITERIA_ANALYSIS,
      objectMapper.readValue("\"multicriteria_report\"", ReportTypes.class));
  }

  @Test
  void rejectsUnknownValue() {
    assertThrows(IllegalArgumentException.class, () -> ReportTypes.fromValue("UNSUPPORTED"));
  }
}
