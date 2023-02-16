package com.elit.outildedecision.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = "formStep")
public class FormQuestion {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long questionId;
    private String questionText;
    private String answerText;
    private String questionRef;

    @ManyToOne(
            optional = false
    )
    @JoinColumn(
            name = "step_id",
            referencedColumnName = "stepId"
    )
    @JsonBackReference("question_step")
    FormStep formStep;

    @JsonManagedReference("question_criteria")
    @OneToMany(
            mappedBy = "formQuestion",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<Criteria> criterias;

    @OneToMany(
            mappedBy = "formQuestion",
            fetch = FetchType.LAZY
    )
    @JsonManagedReference("question_brainstorming")
    private List<Brainstorming> brainstormings;

    @Override
    public String toString() {
        return "FormQuestion{"
                + "questionId=" + questionId
                + ", questionText='" + questionText + '\''
                + ", answerText='" + answerText + '\''
                + '}';
    }
}
