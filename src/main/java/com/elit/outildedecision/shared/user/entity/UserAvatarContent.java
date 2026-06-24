package com.elit.outildedecision.shared.user.entity;

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
 * Binary content of a user's uploaded avatar, stored in its own {@code user_avatar_content} table.
 * The primary key is the owning user id (1:1, no ORM relation kept on purpose to avoid eager LOB
 * loading). The file name is kept on {@code User.userAvatarPath}.
 */
@Entity
@Table(name = "user_avatar_content")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "userId")
public class UserAvatarContent implements Serializable {

  /** Primary key: the owning user id. */
  @Id
  private Long userId;

  /** Raw bytes of the avatar image. Length hint forces a LONGBLOB on MySQL, stays portable on H2. */
  @Lob
  @Column(name = "content", length = 26_214_400)
  private byte[] content;
}
