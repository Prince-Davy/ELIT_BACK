package com.elit.outildedecision.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Feedback utilisateur sur une méthode (étape 3 parcours IA).
 *
 * Un feedback est unique pour un triplet (project, user, method).
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "feedback_ia_method")
public class FeedbackIaMethod {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, unique = true)
  private Long id;

  @Column(name = "is_liked", nullable = false)
  private Boolean isLiked;

  /**
   * Champ libre (modifiable) permettant à l'utilisateur de préciser son feedback.
   * Limité à 255 caractères maximum.
   */
  @Size(max = 255, message = "Le feedback ne peut pas dépasser 255 caractères")
  @Column(name = "feedback_answer", length = 255)
  private String feedbackAnswer;

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "project_id", nullable = false)
  private Project project;

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "method_type_name", nullable = false)
  private String method;
}
