package com.elit.outildedecision.ai.answer.entity;

import com.elit.outildedecision.entity.Project;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Builder
@Entity
@Table(name = "ia_answer")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AiAnswer {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "question", nullable = false)
  private String question;

  @Column(name = "category", nullable = true)
  private String category;

  @Column(name = "answer", nullable = false, length = 1000)
  private String answer;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "project_id", nullable = false)
  private Project project;
}
