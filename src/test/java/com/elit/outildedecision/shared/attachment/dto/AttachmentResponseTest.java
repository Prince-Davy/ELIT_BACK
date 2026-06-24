package com.elit.outildedecision.shared.attachment.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AttachmentResponseTest {
  private AttachmentResponse attachmentResponse;

  @BeforeEach
  public void setup() {
    attachmentResponse = new AttachmentResponse();
    attachmentResponse.setAttachmentId(1L);
    attachmentResponse.setContentType("pdf");
    attachmentResponse.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
    attachmentResponse.setFileName("fileName");
    attachmentResponse.setFilePath("/src/files/");
    attachmentResponse.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now().minusMinutes(10)));
    attachmentResponse.setCreatorFirstName("firstName");
    attachmentResponse.setCreatorLastName("LastName");
  }

  @Test
  void getAttachmentId() {
    assertEquals(1L, attachmentResponse.getAttachmentId());
  }

  @Test
  void getFileName() {
    assertEquals("fileName", attachmentResponse.getFileName());
  }

  @Test
  void getFilePath() {
    assertEquals("/src/files/", attachmentResponse.getFilePath());
  }

  @Test
  void getContentType() {
    assertEquals("pdf", attachmentResponse.getContentType());
  }

  @Test
  void getCreatorLastName() {
    assertEquals("LastName", attachmentResponse.getCreatorLastName());
  }

  @Test
  void getCreatorFirstName() {
    assertEquals("firstName", attachmentResponse.getCreatorFirstName());
  }

  @Test
  void setAttachmentId() {
    attachmentResponse.setAttachmentId(2L);
    assertEquals(2L, attachmentResponse.getAttachmentId());
  }

  @Test
  void setFileName() {
    attachmentResponse.setFileName("newName");
    assertEquals("newName", attachmentResponse.getFileName());
  }

  @Test
  void setFilePath() {
    attachmentResponse.setFilePath("NewPath/");
    assertEquals("NewPath/", attachmentResponse.getFilePath());
  }

  @Test
  void setContentType() {
    attachmentResponse.setContentType("new Type");
    assertEquals("new Type", attachmentResponse.getContentType());
  }

  @Test
  void setCreatorLastName() {
    attachmentResponse.setCreatorLastName("new LastName");
    assertEquals("new LastName", attachmentResponse.getCreatorLastName());
  }

  @Test
  void setCreatorFirstName() {
    attachmentResponse.setCreatorFirstName("new firstName");
    assertEquals("new firstName", attachmentResponse.getCreatorFirstName());
  }
}
