package com.elit.outildedecision.collaboration.multicriteria.validator;

import com.elit.outildedecision.collaboration.multicriteria.entity.Criteria;
import com.elit.outildedecision.collaboration.multicriteria.entity.FormQuestion;
import com.elit.outildedecision.collaboration.multicriteria.dto.MultiCriteriaStepRequest;
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
    question1.setQuestionId(1L);
    question1.setQuestionRef("question_1");
    question1.setQuestionText("Question 1");
    question1.setAnswerText("Valid answer");
    Criteria criteria1A = new Criteria();
    criteria1A.setCriteriaName("Clarity");
    criteria1A.setCriteriaValue(3);
    Criteria criteria1B = new Criteria();
    criteria1B.setCriteriaName("Completeness");
    criteria1B.setCriteriaValue(2);
    question1.setCriterias(new HashSet<>(Arrays.asList(criteria1A, criteria1B)));

    question2 = new FormQuestion();
    question2.setQuestionId(2L);
    question2.setQuestionRef("question_2");
    question2.setQuestionText("Question 2");
    question2.setAnswerText(""); // Missing answer
    Criteria criteria2A = new Criteria();
    criteria2A.setCriteriaName("Clarity");
    criteria2A.setCriteriaValue(0); // Missing criteria
    Criteria criteria2B = new Criteria();
    criteria2B.setCriteriaName("Completeness");
    criteria2B.setCriteriaValue(1);
    question2.setCriterias(new HashSet<>(Arrays.asList(criteria2A, criteria2B)));

    question3 = new FormQuestion();
    question3.setQuestionId(3L);
    question3.setQuestionRef("question_3");
    question3.setQuestionText("Question 3");
    question3.setAnswerText("Another valid answer");
    Criteria criteria3A = new Criteria();
    criteria3A.setCriteriaName("Relevance");
    criteria3A.setCriteriaValue(0); // Missing criteria
    question3.setCriterias(new HashSet<>(Arrays.asList(criteria3A)));
  }

  @Test
  void testValidateValidStep() {
    // Create valid step
    MultiCriteriaStepRequest step = new MultiCriteriaStepRequest();
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
    MultiCriteriaStepRequest step = new MultiCriteriaStepRequest();
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
    MultiCriteriaStepRequest step = new MultiCriteriaStepRequest();
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
    MultiCriteriaStepRequest step = new MultiCriteriaStepRequest();
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
    MultiCriteriaStepRequest step = new MultiCriteriaStepRequest();
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
    MultiCriteriaStepRequest step = new MultiCriteriaStepRequest();
    step.setStepId(1L);
    step.setFormQuestions(new HashSet<>());

    MultiCriteriaFormValidator.ValidationResult result =
      MultiCriteriaFormValidator.validateMultiCriteriaStep(step);

    assertFalse(result.hasErrors());
  }
}
