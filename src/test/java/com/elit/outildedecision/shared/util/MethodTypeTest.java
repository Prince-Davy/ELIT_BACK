package com.elit.outildedecision.shared.util;

import com.elit.outildedecision.shared.exception.UnsupportedMethodologyTypeException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MethodTypeTest {

  @Test
  void testToString() {
    MethodType mt = MethodType.SCRUM;
    final String mtString = "scrum";
    assertEquals(mt.toString(), mtString);
  }

  @Test
  void find() throws UnsupportedMethodologyTypeException {
    assertNotNull(MethodType.find("scrum"));
    assertNotNull(MethodType.find("SCRUM"));
    assertNotNull(MethodType.find("kanban"));
    assertNotNull(MethodType.find("KANBAN"));
    assertNotNull(MethodType.find("waterfall"));
    assertNotNull(MethodType.find("WATERFALL"));
    assertThrows(UnsupportedMethodologyTypeException.class, () -> MethodType.find(""));
    assertThrows(UnsupportedMethodologyTypeException.class, () -> MethodType.find("randomstring"));
    assertThrows(UnsupportedMethodologyTypeException.class, () -> MethodType.find(null));
  }
}