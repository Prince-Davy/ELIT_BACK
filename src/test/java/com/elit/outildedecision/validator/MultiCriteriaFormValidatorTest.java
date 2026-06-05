package com.elit.outildedecision.validator;

import com.elit.outildedecision.entity.Criteria;
import com.elit.outildedecision.entity.FormQuestion;
import com.elit.outildedecision.model.MultiCriteriaStepModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for MultiCriteriaFormValidator.
 */
class MultiCriteriaFormValidatorTest {

  private FormQuestion question1;
  private FormQuestion question2;
  private FormQuestion question3;

  @BeforeEach
  void setUp() {
    // Create mock questions with criteria
    question1 = new FormQuestion();
    question1.setQuestionRef("question_1");
    question1.setQuestionText("Question 1");
    question1.setAnswerText("Valid answer");
    Criteria criteria1_1 = new Criteria();
    criteria1_1.setCriteriaName("Clarity");
    criteria1_1.setCriteriaValue(3);
    Criteria criteria1_2 = new Criteria();
    criteria1_2.setCriteriaName("Completeness");
    criteria1_2.setCriteriaValue(2);
    question1.setCriterias(new HashSet<>(Arrays.asList(criteria1_1, criteria1_2)));

    question2 = new FormQuestion();
    question2.setQuestionRef("question_2");
    question2.setQuestionText("Question 2");
    question2.setAnswerText(""); // Missing answer
    Criteria criteria2_1 = new Criteria();
    criteria2_1.setCriteriaName("Clarity");
    criteria2_1.setCriteriaValue(0); // Missing criteria
    Criteria criteria2_2 = new Criteria();
    criteria2_2.setCriteriaName("Completeness");
    criteria2_2.setCriteriaValue(1);
    question2.setCriterias(new HashSet<>(Arrays.asList(criteria2_1, criteria2_2)));

    question3 = new FormQuestion();
    question3.setQuestionRef("question_3");
    question3.setQuestionText("Question 3");
    question3.setAnswerText("Another valid answer");
    Criteria criteria3_1 = new Criteria();
    criteria3_1.setCriteriaName("Relevance");
    criteria3_1.setCriteriaValue(0); // Missing criteria
    question3.setCriterias(new HashSet<>(Arrays.asList(criteria3_1)));
  }

  @Test
  void testValidateValidStep() {
    // Create valid step
    MultiCriteriaStepModel step = new MultiCriteriaStepModel();
    step.setStepId(1L);
    step.setStepName("Step 1");
    step.setFormQuestions(new HashSet<>(Arrays.asList(question1)));

    MultiCriteriaFormValidator.ValidationResult result =
      MultiCriteriaFormValidator.validateMultiCriteriaStep(step);

    assertFalse(result.hasErrors());
    assertTrue(result.getMissingTextAnswers().isEmpty());
    assertTrue(result.getMissingCriteria().isEmpty());
  }

  @Test
  void testValidateMissingTextAnswer() {
    MultiCriteriaStepModel step = new MultiCriteriaStepModel();
    step.setStepId(1L);
    step.setFormQuestions(new HashSet<>(Arrays.asList(question2)));

    MultiCriteriaFormValidator.ValidationResult result =
      MultiCriteriaFormValidator.validateMultiCriteriaStep(step);

    assertTrue(result.hasErrors());
    assertTrue(result.getMissingTextAnswers().contains(1));
    assertFalse(result.getMissingTextAnswers().isEmpty());
  }

  @Test
  void testValidateMissingCriteria() {
    MultiCriteriaStepModel step = new MultiCriteriaStepModel();
    step.setStepId(1L);
    step.setFormQuestions(new HashSet<>(Arrays.asList(question3)));

    MultiCriteriaFormValidator.ValidationResult result =
      MultiCriteriaFormValidator.validateMultiCriteriaStep(step);

    assertTrue(result.hasErrors());
    assertTrue(result.getMissingCriteria().contains(1));
    assertFalse(result.getMissingCriteria().isEmpty());
  }

  @Test
  void testValidateMultipleErrors() {
    MultiCriteriaStepModel step = new MultiCriteriaStepModel();
    step.setStepId(1L);
    step.setFormQuestions(new HashSet<>(Arrays.asList(question1, question2, question3)));

    MultiCriteriaFormValidator.ValidationResult result =
      MultiCriteriaFormValidator.validateMultiCriteriaStep(step);

    assertTrue(result.hasErrors());
    assertTrue(result.getMissingTextAnswers().contains(2));
    assertTrue(result.getMissingCriteria().contains(2));
    assertTrue(result.getMissingCriteria().contains(3));
  }

  @Test
  void testBuildErrorMessage() {
    MultiCriteriaStepModel step = new MultiCriteriaStepModel();
    step.setStepId(1L);
    step.setFormQuestions(new HashSet<>(Arrays.asList(question1, question2, question3)));

    MultiCriteriaFormValidator.ValidationResult result =
      MultiCriteriaFormValidator.validateMultiCriteriaStep(step);

    String errorMessage = result.buildErrorMessage();

    assertTrue(errorMessage.contains("critères d'évaluation"));
    assertTrue(errorMessage.contains("réponses"));
    assertTrue(errorMessage.contains("2"));
    assertTrue(errorMessage.contains("3"));
  }

  @Test
  void testValidateNullStep() {
    MultiCriteriaFormValidator.ValidationResult result =
      MultiCriteriaFormValidator.validateMultiCriteriaStep(null);

    assertFalse(result.hasErrors());
    assertTrue(result.getMissingTextAnswers().isEmpty());
    assertTrue(result.getMissingCriteria().isEmpty());
  }

  @Test
  void testValidateEmptyQuestions() {
    MultiCriteriaStepModel step = new MultiCriteriaStepModel();
    step.setStepId(1L);
    step.setFormQuestions(new HashSet<>());

    MultiCriteriaFormValidator.ValidationResult result =
      MultiCriteriaFormValidator.validateMultiCriteriaStep(step);

    assertFalse(result.hasErrors());
  }
}
