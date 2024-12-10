package com.elit.outildedecision.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProjectPropertyKeyTests {
  @Test
  void testToString(){

    ProjectPropertyKey projectPropertyKey = new ProjectPropertyKey();
    projectPropertyKey.setProjectId(1L);
    final String projectId = "1";
    assertEquals(projectId, projectPropertyKey.getProjectId().toString());
  }
}
