package com.elit.outildedecision.shared.attachment.entity;

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
 * Binary content of an {@link Attachment}, stored in its own {@code attachment_content} table so the
 * attachment metadata listing never loads the blob. The primary key is the owning attachment id
 * (1:1, no ORM relation kept on purpose to avoid eager LOB loading).
 */
@Entity
@Table(name = "attachment_content")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "attachmentId")
public class AttachmentContent implements Serializable {

  /** Primary key: the owning attachment id. */
  @Id
  private Long attachmentId;

  /** Raw bytes of the attachment file. Length hint forces a LONGBLOB on MySQL, stays portable on H2. */
  @Lob
  @Column(name = "content", length = 26_214_400)
  private byte[] content;
}
