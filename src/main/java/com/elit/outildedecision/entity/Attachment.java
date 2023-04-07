
package com.elit.outildedecision.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;


@Entity
@Table(name = "attachment")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attachment implements Serializable {

  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long attachmentId;

  private String fileName;
  private String filePath;
  private String contentType;

  @ManyToOne
  @JoinColumn(name = "project_Id", referencedColumnName = "projectId")
  private Project project;

  /**
   * return project.
   * @return project
   */
  @JsonBackReference("project_attachment")
  public Project getProject() {
    return this.project;
  }

}
