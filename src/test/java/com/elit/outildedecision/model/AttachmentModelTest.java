package com.elit.outildedecision.model;

import com.elit.outildedecision.entity.Attachment;
import com.elit.outildedecision.entity.Project;
import com.elit.outildedecision.entity.ProjectUserPermission;
import com.elit.outildedecision.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AttachmentModelTest {
  private ArrayList<MultipartFile> files;

  @BeforeEach
  public void setup() {
    MockMultipartFile fileMultipart =
      new MockMultipartFile(
        "files",
        "test contract.pdf",
        MediaType.APPLICATION_PDF_VALUE,
        "<<pdf data>>".getBytes(StandardCharsets.UTF_8));
    files = new ArrayList<>();
    files.add(fileMultipart);
  }
  @Test
  void getAttachments() {
    AttachmentModel attachmentModel = new AttachmentModel();

    ArrayList<MultipartFile> listAttachment = new ArrayList<>();

    attachmentModel.setAttachments(listAttachment);

    assertEquals(listAttachment, attachmentModel.getAttachments());
  }

  @Test
  void setAttachments() {
    AttachmentModel attachmentModel = new AttachmentModel();
    attachmentModel.setAttachments(files);
    assertEquals(files, attachmentModel.getAttachments());
  }

  @Test
  void testEquals() {
    AttachmentModel attachmentModel = new AttachmentModel();
    attachmentModel.setAttachments(files);
    assertEquals(files.get(0), attachmentModel.getAttachments().get(0));
  }

  @Test
  void testToString() {
    AttachmentModel attachmentModel = new AttachmentModel();
    attachmentModel.setAttachments(files);
    assertEquals(files.get(0).getName(), "files");
  }
}
