package com.elit.outildedecision.ai.answer.entity;

import com.elit.outildedecision.shared.kernel.entity.Project;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;



/**
 * Represents an AI-generated answer linked to a specific project.
 * This class is a JPA entity mapped to the "ia_answer" table.
 * Lombok annotations used:
 * - @Builder: Enables the builder pattern for object creation.
 * - @Getter / @Setter: Automatically generates getters and setters.
 * - @AllArgsConstructor: Generates a constructor with all fields.
 * - @NoArgsConstructor: Generates a no-argument constructor.
 * JPA annotations used:
 * - @Entity: Marks this class as a JPA entity.
 * - @Table(name = "ia_answer"): Maps the entity to the "ia_answer" table.
 * - @Id and @GeneratedValue: Specifies the primary key and its generation strategy.
 * - @Column: Maps fields to table columns with constraints.
 * - @ManyToOne and @JoinColumn: Defines a many-to-one relationship with the Project entity.
 */
@Builder
@Entity
@Table(name = "ia_answer")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class AiAnswer {

  /**
   * Unique identifier for the AI answer.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * The question that was asked to generate the AI answer.
   * This field is required.
   */
  @Column(name = "question", nullable = false)
  private String question;

  /**
   * Optional category used to classify the question or answer.
   */
  @Column(name = "category", nullable = true)
  private String category;

  /**
   * The AI-generated answer to the question.
   * This field is required and limited to 1000 characters.
   */
  @Column(name = "answer", nullable = false, length = 1000)
  private String answer;

  /**
   * The project to which this answer belongs.
   * This is a required field and is lazily loaded.
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "project_id", nullable = false)
  private Project project;
}
