package com.elit.outildedecision.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

 class AttachmentTests {
    @Test
    void testGetProject(){
        Attachment attachment = new Attachment();
        Project p = new Project();
        attachment.setProject(p);
        assertEquals(p, attachment.getProject());
    }
}
