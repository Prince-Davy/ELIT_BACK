package com.elit.outildedecision.model;

import com.elit.outildedecision.util.AnswerType;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class SurveyAnswerRequestModel {
    private long questionId;
    private long userId;
    // selectedValue or freeAnswer
    private AnswerType answerType;
    @NotBlank(message = "La reponse ne doit pas être vide")
    private String answerValue;
}
