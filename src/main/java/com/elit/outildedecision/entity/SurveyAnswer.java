package com.elit.outildedecision.entity;

import com.elit.outildedecision.util.AnswerType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * class used as model to :
 *  build the survey answer table in database
 *  manage answer data in database
 */
@Entity
@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SurveyAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne()
    private User user;
    @ManyToOne()
    private SurveyQuestion question;

    @Enumerated(EnumType.STRING)
    private AnswerType type;

    private String answerValue;


}