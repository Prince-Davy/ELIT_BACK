
package com.elit.outildedecision.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.sql.Timestamp;


/**
 * Entity representing a file attachment linked to a project.
 * This class is mapped to the "attachment" table in the database.
 */
@Entity
@Table(name = "attachment")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attachment implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Unique identifier for the attachment.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long attachmentId;

  /**
   * The name of the uploaded file.
   */
  private String fileName;

  /**
   * The path where the file is stored.
   */
  private String filePath;

  /**
   * The MIME type of the file.
   */
  private String contentType;

  /**
   * The project to which this attachment belongs.
   */
  @ManyToOne
  @JoinColumn(name = "project_Id", referencedColumnName = "projectId")
  private Project project;

  /**
   * Timestamp indicating when the attachment was created.
   * Automatically set and formatted for JSON output.
   */
  @CreationTimestamp
  @JsonFormat(pattern = "dd-MM-yyyy", timezone = "Europe/Paris")
  private Timestamp createdAt;

  /**
   * Timestamp indicating when the attachment was last updated.
   * Automatically set and formatted for JSON output.
   */
  @UpdateTimestamp
  @JsonFormat(pattern = "dd-MM-yyyy", timezone = "Europe/Paris")
  private Timestamp updatedAt;

  /**
   * Returns the project associated with this attachment.
   * Used to prevent circular references in JSON serialization.
   *
   * @return the associated project
   */
  @JsonBackReference("project_attachment")
  public Project getProject() {
    return this.project;
  }

  /**
   * The user who created the attachment.
   */
  @ManyToOne
  @JoinColumn(name = "CREATOR_ID", referencedColumnName = "userId")
  private User creator;
}
