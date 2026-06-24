package com.elit.outildedecision.shared.project.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

/**
 * Request payload used to create or update a project, carrying its name, description,
 * the list of contributors to attach and the files to upload.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ProjectRequest {

  /**
   * The name of the project.
   */
  private String name;

  /**
   * The description of the project.
   */
  private String description;

  /**
   * A list of contributors to attach to the project.
   */
  private ArrayList<ContributorRequest> contributers;

  /**
   * A list of files associated with the project.
   */
  private ArrayList<MultipartFile> files;
}
