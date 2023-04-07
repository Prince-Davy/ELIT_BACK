package com.elit.outildedecision.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Timestamp;

import org.mockito.Mockito;
import org.junit.jupiter.api.Test;

 class ProjectTests {
    @Test
    void testToString(){
        final Timestamp date = Mockito.mock(Timestamp.class);
        Project project = new Project();
        project.setName("name");
        project.setDescription("description");
        project.setProjectId(1L);
        project.setFolderPath("/");
        project.setCreatedAt(date);
        project.setUpdatedAt(date);
        final String pString = "Project{"
        + "projectId="
        + 1
        + ", name='"
        + "name"
        + '\''
        + ", description='"
        + "description"
        + '\''
        + ", folderPath='"
        + "/"
        + '\''
        + ", createdAt="
        + date
        + ", updatedAt="
        + date
        + '}';
        assertEquals(pString,project.toString());
    }
}
