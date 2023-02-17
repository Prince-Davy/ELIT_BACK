package com.elit.outildedecision.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Methodology{
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
  public String toString(){
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
