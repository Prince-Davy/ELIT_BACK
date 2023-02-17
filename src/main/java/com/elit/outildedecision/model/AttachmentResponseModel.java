package com.elit.outildedecision.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttachmentResponseModel {
  private Long attachmentId;
  private String fileName;
  private String filePath;
  private String contentType;
}
