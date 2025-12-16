package com.elit.outildedecision.audit.entity.comment;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.SuperBuilder;
import jakarta.persistence.Inheritance;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.DiscriminatorType;



@Entity
@Table(
  name = "global_comment",
  uniqueConstraints = @UniqueConstraint(columnNames = { "method_type", "level" })
)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "method_type", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public abstract class GlobalComment extends AbstractComment {

  /**
   * the title of the text .
   */
  @Column(name = "title", nullable = false)
  private String title;
}
