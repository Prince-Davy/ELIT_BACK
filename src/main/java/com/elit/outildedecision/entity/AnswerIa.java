package com.elit.outildedecision.entity;

import com.elit.outildedecision.config.View;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "answer_ia")
@IdClass(AnswerIaKey.class)
public class AnswerIa {

  @Id
  @Column(name = "question", nullable = false)
  @JsonView(View.AnswerIa.class)
  private String question;

  @Column(name = "category", nullable = false)
  @JsonView(View.AnswerIa.class)
  private String category;

  @Column(name = "answer", nullable = false)
  @JsonView(View.AnswerIa.class)
  private String answer;

  @Id
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "project_id", nullable = false)
  @JsonView(View.AnswerIa.class)
  private Project project;

  /**
   * get answer.
   * @return answer
   */
  public String getAnswer() {
    return answer;
  }

  /**
   * set answer.
   * @param answer answer
   */
  public void setAnswer(final String answer) {
    this.answer = answer;
  }


}
