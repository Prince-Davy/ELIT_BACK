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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * AI feedback left by a user on the AI methodology recommendation of a project.
 */
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "feedback_ia")
public class FeedbackIa {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, unique = true)
  private Long id;

  @Column(name = "isLiked", nullable = false)
  private Boolean isLiked;

  @Column(name = "methodology")
  private String methodology;

  @Column(name = "feedbackAnswer")
  private String feedbackAnswer;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "project_id", nullable = false)
  private Project project;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;
}
