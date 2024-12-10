package com.elit.outildedecision.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AttachmentResponseModelTest {
private AttachmentResponseModel attachmentResponseModel;
  @BeforeEach
  public void setup() {
    attachmentResponseModel = new AttachmentResponseModel();
    attachmentResponseModel.setAttachmentId(1L);
    attachmentResponseModel.setContentType("pdf");
    attachmentResponseModel.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
    attachmentResponseModel.setFileName("fileName");
    attachmentResponseModel.setFilePath("/src/files/");
    attachmentResponseModel.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now().minusMinutes(10)));
    attachmentResponseModel.setCreatorFirstName("firstName");
    attachmentResponseModel.setCreatorLastName("LastName");
  }

  @Test
  void getAttachmentId() {

    assertEquals(1L, attachmentResponseModel.getAttachmentId());

  }

  @Test
  void getFileName() {
    assertEquals("fileName", attachmentResponseModel.getFileName());
  }

  @Test
  void getFilePath() {
    assertEquals("/src/files/", attachmentResponseModel.getFilePath());
  }

  @Test
  void getContentType() {
    assertEquals("pdf", attachmentResponseModel.getContentType());
  }

  @Test
  void getCreatedAt() {
    assertEquals(attachmentResponseModel.getCreatedAt().getTime(), attachmentResponseModel.getCreatedAt().getTime());
  }

  @Test
  void getUpdatedAt() {
    assertEquals(attachmentResponseModel.getUpdatedAt(), attachmentResponseModel.getUpdatedAt());
  }

  @Test
  void getCreatorLastName() {
    assertEquals("LastName", attachmentResponseModel.getCreatorLastName());
  }

  @Test
  void getCreatorFirstName() {
    assertEquals("firstName", attachmentResponseModel.getCreatorFirstName());
  }

  @Test
  void setAttachmentId() {
    attachmentResponseModel.setAttachmentId(2L);
    assertEquals(2L, attachmentResponseModel.getAttachmentId());
  }

  @Test
  void setFileName() {
    attachmentResponseModel.setFileName("newName");
    assertEquals("newName", attachmentResponseModel.getFileName());
  }

  @Test
  void setFilePath() {
    attachmentResponseModel.setFilePath("NewPath/");
    assertEquals("NewPath/", attachmentResponseModel.getFilePath());
  }

  @Test
  void setContentType() {
    attachmentResponseModel.setContentType("new Type");
    assertEquals("new Type", attachmentResponseModel.getContentType());
  }

  @Test
  void setCreatedAt() {
    attachmentResponseModel.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
    assertEquals(attachmentResponseModel.getCreatedAt(), attachmentResponseModel.getCreatedAt());
  }

  @Test
  void setUpdatedAt() {
    attachmentResponseModel.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
    assertEquals(attachmentResponseModel.getUpdatedAt(), attachmentResponseModel.getUpdatedAt());
  }

  @Test
  void setCreatorLastName() {
    attachmentResponseModel.setCreatorLastName("new LastName");
    assertEquals("new LastName",attachmentResponseModel.getCreatorLastName());
  }

  @Test
  void setCreatorFirstName() {
    attachmentResponseModel.setCreatorFirstName("new firstName");
    assertEquals("new firstName", attachmentResponseModel.getCreatorFirstName());
  }
}
