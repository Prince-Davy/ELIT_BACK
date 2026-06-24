package com.elit.outildedecision.shared.methodology.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.List;

/** Methodology class. */
@Entity
@Table(name = "methodology")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Methodology implements Serializable {

  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long methodologyId;

  /* Classic, Hybrid ou Agile */
  private String name;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(columnDefinition = "TEXT")
  private String disadvantages;

  @Column(columnDefinition = "TEXT")
  private String advantages;

  @OneToMany(mappedBy = "methodology", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @JsonManagedReference("methodology_predictibility")
  private List<Method> methods;

  /**
   * @return String
   */
  @Override
  public String toString() {
    return "Methodology{"
        + "methodologyId="
        + methodologyId
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
