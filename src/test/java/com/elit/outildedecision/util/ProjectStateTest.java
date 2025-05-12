package com.elit.outildedecision.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProjectStateTest {

  @Test
  void testEnumValues() {
    assertEquals("ALL", ProjectState.ALL.name());
    assertEquals("IN_PROGRESS", ProjectState.IN_PROGRESS.name());
    assertEquals("COMPLETED", ProjectState.COMPLETED.name());
    assertEquals("PENDING", ProjectState.PENDING.name());
  }

  @Test
  void testLabels() {
    assertEquals("ALL", ProjectState.ALL.getLabel());
    assertEquals("IN PROGRESS", ProjectState.IN_PROGRESS.getLabel());
    assertEquals("COMPLETED", ProjectState.COMPLETED.getLabel());
    assertEquals("PENDING", ProjectState.PENDING.getLabel());
  }

  @Test
  void testToString() {
    assertEquals("ALL", ProjectState.ALL.toString());
    assertEquals("IN PROGRESS", ProjectState.IN_PROGRESS.toString());
    assertEquals("COMPLETED", ProjectState.COMPLETED.toString());
    assertEquals("PENDING", ProjectState.PENDING.toString());
  }

  @Test
  void testValueOfEnum() {
    assertEquals(ProjectState.ALL, ProjectState.valueOf("ALL"));
    assertEquals(ProjectState.IN_PROGRESS, ProjectState.valueOf("IN_PROGRESS"));
    assertEquals(ProjectState.COMPLETED, ProjectState.valueOf("COMPLETED"));
    assertEquals(ProjectState.PENDING, ProjectState.valueOf("PENDING"));
  }
}
