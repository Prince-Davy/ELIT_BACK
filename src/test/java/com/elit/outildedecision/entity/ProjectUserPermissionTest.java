package com.elit.outildedecision.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProjectUserPermissionTest {
  @Test
  void testToString(){

    ProjectUserPermission projectUserPermission = new ProjectUserPermission();
    projectUserPermission.setPermission("permission");
    final String permissionString = "permission";
    assertEquals(permissionString, projectUserPermission.getPermission());
  }
}
