package com.elit.outildedecision.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
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
}
