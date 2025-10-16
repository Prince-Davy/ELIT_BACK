package com.elit.outildedecision.audit.entity.comment;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.SuperBuilder;
import javax.persistence.Inheritance;
import javax.persistence.Table;
import javax.persistence.Entity;
import javax.persistence.Column;
import javax.persistence.UniqueConstraint;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.InheritanceType;
import javax.persistence.DiscriminatorType;



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
