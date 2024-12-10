package com.elit.outildedecision.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "report")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Report implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long reportId;

  @ManyToOne(optional = false)
  @JoinColumn(name = "project_id", referencedColumnName = "projectId")
  @JsonBackReference("report_project")
  private Project project;

  private Long analyseId;

  private String reportPath;

  private String type;

  private Integer iteration;

  @CreationTimestamp
  @JsonFormat(pattern = "dd-MM-yyyy", timezone = "Europe/Paris")
  private Timestamp createdAt;

  /**
   * get type of report.
   * @return type
   */
  public String getType() {
    return type;
  }

  /**
   * set type of report.
   * @param type type
   */
  public void setType(final String type) {
    this.type = type;
  }
}
