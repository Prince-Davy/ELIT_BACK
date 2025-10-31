package com.elit.outildedecision.ai.methodology.evaluation.entity;

import com.elit.outildedecision.ai.methodology.evaluation.converter.PredictionConverter;
import com.elit.outildedecision.entity.Project;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Entity representing the evaluation of a methodology for a given project.
 * This class is mapped to the "methodology_evaluation" table in the database.
 *
 * Lombok annotations used:
 * - @Data: Generates getters, setters, equals, hashCode, and toString methods.
 * - @Builder: Enables the builder pattern for object creation.
 * - @AllArgsConstructor: Generates a constructor with all fields.
 * - @NoArgsConstructor: Generates a no-argument constructor.
 *
 * JPA annotations used:
 * - @Entity: Marks this class as a JPA entity.
 * - @Table(name = "methodology_evaluation"): Maps the entity to the corresponding table.
 * - @Id and @GeneratedValue: Specifies the primary key and its generation strategy.
 * - @ManyToOne and @JoinColumn: Defines a many-to-one relationship with the Project entity.
 * - @Column: Maps fields to table columns with constraints.
 * - @Convert: Applies a custom converter for storing questionScores as JSON.
 * - @CreatedDate: Automatically sets the creation timestamp.
 * - @EntityListeners: Enables auditing features like automatic timestamping.
 */
@Entity
@Table(name = "methodology_evaluation")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class MethodologyEvaluation {

  /**
   * Unique identifier for the methodology evaluation.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * The project associated with this evaluation.
   * This field is required.
   */
  @ManyToOne
  @JoinColumn(nullable = false)
  @JsonBackReference("methodology_evaluations_project")
  private Project project;

  /**
   * The name of the evaluated methodology.
   */
  @Column(name = "methodology")
  private String methodology;

  /**
   * The score assigned to the methodology.
   */
  @Column(name = "score")
  private double score;

  /**
   * A map of question identifiers to their respective scores.
   * Stored as JSON in the database using a custom converter.
   */
  @Convert(converter = PredictionConverter.class)
  @Column(name = "questionScores", columnDefinition = "json")
  private Map<String, Double> questionScores = new HashMap<>();

  /**
   * Timestamp indicating when the evaluation was created.
   * Automatically set and not updatable.
   */
  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;
}
