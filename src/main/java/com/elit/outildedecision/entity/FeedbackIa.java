package com.elit.outildedecision.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;


@Entity
@Data
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

  /**
   * get methodology.
   * @return methodology
   */
  public String getMethodology() {
    return methodology;
  }

  /**
   * set methodology.
   * @param methodology methodo
   */
  public void setMethodology(final String methodology) {
    this.methodology = methodology;
  }
}
