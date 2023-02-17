package com.elit.outildedecision.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentModel {
  private ArrayList<MultipartFile> attachments;
}
