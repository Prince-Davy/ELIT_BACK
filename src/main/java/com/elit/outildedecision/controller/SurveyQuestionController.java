package com.elit.outildedecision.controller;

import com.elit.outildedecision.entity.SurveyQuestion;
import com.elit.outildedecision.exception.QuestionFormatNotValidException;
import com.elit.outildedecision.service.implementation.SurveyQuestionServiceImpl;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * class used to root API calls for survey questions management.
 * Contains methods to call to display, create, delete or modify survey questions.
 */
@RestController
@RequestMapping("/api/survey/question")
public class SurveyQuestionController {

    private final SurveyQuestionServiceImpl questionService;

    public SurveyQuestionController(SurveyQuestionServiceImpl questionService) {
        this.questionService = questionService;
    }

    /**
     * method called to add a new question to the survey
     * @param question represents the new question to add on survey
     * @return added question. return needed for API testing
     */
    @PostMapping("")
    public SurveyQuestion createQuestion(@RequestBody(required = false) SurveyQuestion question) throws QuestionFormatNotValidException {
        return questionService.createQuestion(question);
    }

    /**
     * method called to modify an existing question.
     * @param question given in parameter and contains modification to apply.
     * @return updated question. return needed for API testing
     */
    @PutMapping("/{questionId}") // or patch ?
    public SurveyQuestion modifyQuestion(@PathVariable Long questionId,@RequestBody(required = false) SurveyQuestion question) throws QuestionFormatNotValidException {
        return questionService.modifyQuestion(questionId, question);
    }

    /**
     * method called to remove a question on survey
     * @param questionId given in parameter and represents the id of the existing question to delete
     * @return  deleted question. return needed for API testing
     */
    @DeleteMapping("/{questionId}")
    public Optional<SurveyQuestion> deleteQuestion(@PathVariable Long questionId) {
            return questionService.deleteQuestion(questionId);
    }

    /**
     * method called to select and display an existing question
     * @param questionId given in parameter and represents the desired question to display
     * @return selected question.
     */
    @GetMapping("/{questionId}")
    public Optional<SurveyQuestion> selectQuestion(@PathVariable Long questionId) {
            return questionService.selectQuestion(questionId);
    }

    /**
     * method called to display all questions that belong to the survey
     * @return a list of all existing questions on survey
     */
    @GetMapping("")
    public List<SurveyQuestion> getQuestionsList() {
        return questionService.getQuestionsList();
    }
}
