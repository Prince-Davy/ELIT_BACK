package com.elit.outildedecision.ai.result.entity;

import com.elit.outildedecision.shared.kernel.entity.Project;
import lombok.AllArgsConstructor;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * AI prediction result persisted for a project (table {@code result_ia}, schema frozen).
 */
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "result_ia")
public class ResultAi {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, unique = true)
  private Long id;

  @Column(name = "predicted_methode")
  private String predictedMethod;

  @Column(name = "predicted_probability")
  private Double predictedProba;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "project_id", nullable = false)
  private Project project;
}
