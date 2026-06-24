package com.elit.outildedecision.collaboration.multicriteria.entity;

import com.elit.outildedecision.collaboration.brainstorming.entity.Brainstorming;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

/** Form question class. */
@Entity
@Table(name = "form_question")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "questionId")
public class FormQuestion implements Serializable {

  private static final long serialVersionUID = 1L;
  @ManyToOne(optional = false)
  @JoinColumn(name = "step_id", referencedColumnName = "stepId")
  @JsonBackReference("question_step")
  private FormStep formStep;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long questionId;

  private String questionText;
  private String answerText;
  private String questionRef;
  @Builder.Default
  private Boolean updateQuestion = false;
  @JsonManagedReference("question_criteria")
  @OneToMany(
      mappedBy = "formQuestion",
      fetch = FetchType.EAGER,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private Set<Criteria> criterias;

  @OneToMany(mappedBy = "formQuestion", fetch = FetchType.LAZY)
  @JsonManagedReference("question_brainstorming")
  private List<Brainstorming> brainstormings;

  /**
   * @return String
   */
  @Override
  public String toString() {
    return "FormQuestion{"
        + "questionId="
        + questionId
        + ", questionText='"
        + questionText
        + '\''
        + ", answerText='"
        + answerText
        + '\''
        + '}';
  }
}
