package com.elit.outildedecision.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;


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
