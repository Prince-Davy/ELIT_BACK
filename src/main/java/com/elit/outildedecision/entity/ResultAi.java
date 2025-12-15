package com.elit.outildedecision.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToOne;


@Entity
@Data
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

  /**
   * get predicted Method.
   * @return predictedMethod
   */
  public String getPredictedMethod() {
    return predictedMethod;
  }

  /**
   * set predicted Method.
   * @param predictedMethod predictedMethod
   */
  public void setPredictedMethod(final String predictedMethod) {
    this.predictedMethod = predictedMethod;
  }

}
