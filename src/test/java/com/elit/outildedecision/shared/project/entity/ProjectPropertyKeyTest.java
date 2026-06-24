package com.elit.outildedecision.shared.project.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProjectPropertyKeyTest {
  @Test
  void testToString(){

    ProjectPropertyKey projectPropertyKey = new ProjectPropertyKey();
    projectPropertyKey.setProjectId(1L);
    final String projectId = "1";
    assertEquals(projectId, projectPropertyKey.getProjectId().toString());
  }
}
