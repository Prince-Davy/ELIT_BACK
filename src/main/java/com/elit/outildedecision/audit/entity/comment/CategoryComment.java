package com.elit.outildedecision.audit.entity.comment;

import com.elit.outildedecision.shared.util.QuestionCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.SuperBuilder;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.EnumType;
import jakarta.persistence.Column;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;

/** Category comment class. */
@Entity
@Table(name = "category_comment", uniqueConstraints = @UniqueConstraint(columnNames = {"category", "level", "method_type"}))
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "method_type", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class CategoryComment extends AbstractComment {

  /**
   * The category of questions this comment is associated with.
   * Stored as a string in the database.
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "category", nullable = false)
  private QuestionCategory category;
}


