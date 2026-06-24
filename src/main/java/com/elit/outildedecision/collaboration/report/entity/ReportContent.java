package com.elit.outildedecision.collaboration.report.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Binary content of a {@link Report}, stored in its own {@code report_content} table so the report
 * metadata listing never loads the blob. The primary key is the owning report id (1:1, no ORM
 * relation kept on purpose to avoid eager LOB loading).
 */
@Entity
@Table(name = "report_content")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "reportId")
public class ReportContent implements Serializable {

  /** Primary key: the owning report id. */
  @Id
  private Long reportId;

  /** Raw bytes of the report file. Length hint forces a LONGBLOB on MySQL, stays portable on H2. */
  @Lob
  @Column(name = "content", length = 26_214_400)
  private byte[] content;
}
