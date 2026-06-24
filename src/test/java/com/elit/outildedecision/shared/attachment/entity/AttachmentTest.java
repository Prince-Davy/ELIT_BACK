package com.elit.outildedecision.shared.attachment.entity;

import com.elit.outildedecision.shared.kernel.entity.Project;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

 class AttachmentTest {
    @Test
    void testGetProject(){
        Attachment attachment = new Attachment();
        Project p = new Project();
        attachment.setProject(p);
        assertEquals(p, attachment.getProject());
    }
}
