package com.elit.outildedecision.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import org.checkerframework.checker.initialization.qual.Initialized;
import org.springframework.boot.context.properties.bind.DefaultValue;

import javax.persistence.*;

@Entity @Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude="formQuestion")
public class Criteria {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long criteriaId;

    private String criteriaName;

    private int criteriaValue;

    private float score;

    @JsonBackReference("question_criteria")
    @ManyToOne(
            fetch = FetchType.EAGER
    )
    @JoinColumn(
            name = "question_id",
            referencedColumnName = "questionId"
    )
    private FormQuestion formQuestion;


    @Override
    public String toString() {
        return "Criteria{" +
                "criteriaId=" + criteriaId +
                ", criteriaName='" + criteriaName + '\'' +
                ", criteriaValue=" + criteriaValue +
                '}';
    }
}
