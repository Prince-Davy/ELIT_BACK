package com.elit.outildedecision.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;


@Entity
@Table(name = "method")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Method implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  /* Scrum, Kanban ... */
  private String name;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(columnDefinition = "TEXT")
  private String disadvantages;

  @Column(columnDefinition = "TEXT")
  private String advantages;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "methodology_id", referencedColumnName = "methodologyId")
  @JsonBackReference("methodology_predictibility")
  private Methodology methodology;

  /**
   * @return String
   */
  @Override
  public String toString() {
    return "Method{"
        + "id="
        + id
        + ", name='"
        + name
        + '\''
        + ", description='"
        + description
        + '\''
        + ", disadvantages='"
        + disadvantages
        + '\''
        + ", advantages='"
        + advantages
        + '\''
        + '}';
  }
}
