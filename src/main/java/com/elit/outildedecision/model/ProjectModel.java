package com.elit.outildedecision.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectModel implements Serializable {
  private String name;
  private String description;
  private ArrayList<ContributerModel> contributers;
  private ArrayList<MultipartFile> files;
}
