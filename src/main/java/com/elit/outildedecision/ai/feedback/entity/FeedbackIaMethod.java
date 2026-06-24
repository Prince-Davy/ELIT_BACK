package com.elit.outildedecision.ai.feedback.entity;

import com.elit.outildedecision.shared.kernel.entity.Project;
import com.elit.outildedecision.shared.kernel.entity.User;
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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Feedback utilisateur sur une methode (etape 3 parcours IA).
 *
 * <p>Un feedback est unique pour un triplet (project, user, method).
 */
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
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
   * Champ libre (modifiable) permettant a l'utilisateur de preciser son feedback.
   * Limite a 255 caracteres maximum.
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
