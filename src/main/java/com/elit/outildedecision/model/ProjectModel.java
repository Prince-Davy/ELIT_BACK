package com.elit.outildedecision.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import java.util.ArrayList;

/**
 * This class represents a project model containing information about a project.
 * It is used to store and manage data associated with projects, including name, description, a list of contributors, and a list of files.
 * It uses Lombok annotations to generate constructors, getters, and setters.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectModel {

  /**
   * The name of the project.
   */
  private String name;

  /**
   * The description of the project.
   */
  private String description;

  /**
   * A list of contributor models.
   */
  private ArrayList<ContributerModel> contributers;

  /**
   * A list of files associated with the project.
   */
  private ArrayList<MultipartFile> files;
}
