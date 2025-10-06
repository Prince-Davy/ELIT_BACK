package com.elit.outildedecision.audit.entity;

import com.elit.outildedecision.util.QuestionCategory;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.EnumType;
import javax.persistence.Column;
import javax.persistence.UniqueConstraint;
import javax.persistence.InheritanceType;
import javax.persistence.Inheritance;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;

@Entity
@Table(name = "category_comment", uniqueConstraints = @UniqueConstraint(columnNames = {"category", "level", "method_type"}))
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "method_type", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CategoryComment extends AbstractComment {

  /**
   * The category of questions this comment is associated with.
   * Stored as a string in the database.
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "category", nullable = false)
  private QuestionCategory category;

}


