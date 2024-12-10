package com.elit.outildedecision.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

/**
 * This class represents an attachment model that holds a list of attachments.
 * It is a container for MultipartFile objects, which represent uploaded files received in a multipart request.
 * It uses Lombok annotations to generate constructors, getters, and setters.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentModel {

  /**
   * A list of attachments represented as MultipartFile objects.
   */
  private ArrayList<MultipartFile> attachments;

  /**
   * Get ArrayList<MultipartFile>.
   *
   * @return ArrayList<MultipartFile>
   */
  public ArrayList<MultipartFile> getAttachments() {
    return attachments;
  }

  /**
   * Set attachments.
   *
   * @param attachments ArrayList<MultipartFile>
   */
  public void setAttachments(final ArrayList<MultipartFile> attachments) {
    this.attachments = attachments;
  }
}
