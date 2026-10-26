package com.elit.outildedecision.shared.report.entity;

import com.elit.outildedecision.shared.kernel.entity.Project;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.sql.Timestamp;

import com.elit.outildedecision.shared.util.ReportTypes;
import com.elit.outildedecision.shared.util.ReportTypeConverter;
import jakarta.persistence.Convert;

/**
 * Entity mapping the {@code report} table: a synthesis report file attached to a project analysis.
 */
@Entity
@Table(name = "report")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "reportId")
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

  @Convert(converter = ReportTypeConverter.class)
  private ReportTypes type;

  private Integer iteration;

  @CreationTimestamp
  @JsonFormat(pattern = "dd-MM-yyyy", timezone = "Europe/Paris")
  private Timestamp createdAt;
}
