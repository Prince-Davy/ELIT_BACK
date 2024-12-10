package com.elit.outildedecision.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;


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
