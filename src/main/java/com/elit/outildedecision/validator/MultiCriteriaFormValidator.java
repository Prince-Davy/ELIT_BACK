package com.elit.outildedecision.validator;

import com.elit.outildedecision.entity.Criteria;
import com.elit.outildedecision.entity.FormQuestion;
import com.elit.outildedecision.model.MultiCriteriaStepModel;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Validator for multi-criteria analysis forms.
 * Validates that all required fields (text answers and evaluation criteria) are properly filled.
 */
public class MultiCriteriaFormValidator {

  /**
   * Validates a multi-criteria analysis step model.
   * Checks that all questions have text answers and all evaluation criteria are rated (not 0).
   *
   * @param multiCriteriaStep the step model to validate
   * @return a ValidationResult containing detailed information about validation errors
   */
  public static ValidationResult validateMultiCriteriaStep(final MultiCriteriaStepModel multiCriteriaStep) {
    ValidationResult result = new ValidationResult();
    
    if (multiCriteriaStep == null || multiCriteriaStep.getFormQuestions() == null) {
      return result;
    }

    List<FormQuestion> formQuestions = new ArrayList<>(multiCriteriaStep.getFormQuestions());
    
    // Sort questions by their reference to get consistent numbering
    formQuestions.sort((q1, q2) -> {
      int num1 = extractQuestionNumber(q1.getQuestionRef());
      int num2 = extractQuestionNumber(q2.getQuestionRef());
      return Integer.compare(num1, num2);
    });

    for (int i = 0; i < formQuestions.size(); i++) {
      FormQuestion question = formQuestions.get(i);
      int questionNumber = i + 1;

      // Check for missing text answer
      if (question.getAnswerText() == null || question.getAnswerText().trim().isEmpty()) {
        result.addMissingTextAnswer(questionNumber);
      }

      // Check for missing evaluation criteria
      if (question.getCriterias() != null && !question.getCriterias().isEmpty()) {
        List<Criteria> missingCriteriaList = question.getCriterias().stream()
          .filter(c -> c.getCriteriaValue() == 0)
          .collect(Collectors.toList());

        if (!missingCriteriaList.isEmpty()) {
          result.addMissingCriteria(questionNumber);
        }
      }
    }

    return result;
  }

  /**
   * Extracts the question number from a question reference string.
   * Example: "question_1" -> 1, "question_42" -> 42
   *
   * @param questionRef the question reference string
   * @return the extracted question number, or 0 if not found
   */
  private static int extractQuestionNumber(final String questionRef) {
    if (questionRef == null || questionRef.isEmpty()) {
      return 0;
    }
    
    try {
      String[] parts = questionRef.split("_");
      if (parts.length > 0) {
        return Integer.parseInt(parts[parts.length - 1]);
      }
    } catch (NumberFormatException e) {
      // If parsing fails, return 0
    }
    
    return 0;
  }

  /**
   * Result class containing validation information.
   */
  public static class ValidationResult {
    private final List<Integer> missingTextAnswers = new ArrayList<>();
    private final List<Integer> missingCriteria = new ArrayList<>();

    /**
     * Adds a question number with missing text answer.
     *
     * @param questionNumber the question number
     */
    public void addMissingTextAnswer(final int questionNumber) {
      if (!missingTextAnswers.contains(questionNumber)) {
        missingTextAnswers.add(questionNumber);
      }
    }

    /**
     * Adds a question number with missing evaluation criteria.
     *
     * @param questionNumber the question number
     */
    public void addMissingCriteria(final int questionNumber) {
      if (!missingCriteria.contains(questionNumber)) {
        missingCriteria.add(questionNumber);
      }
    }

    /**
     * Checks if there are any validation errors.
     *
     * @return true if there are validation errors, false otherwise
     */
    public boolean hasErrors() {
      return !missingTextAnswers.isEmpty() || !missingCriteria.isEmpty();
    }

    /**
     * Gets the list of question numbers with missing text answers.
     *
     * @return list of question numbers
     */
    public List<Integer> getMissingTextAnswers() {
      return new ArrayList<>(missingTextAnswers);
    }

    /**
     * Gets the list of question numbers with missing evaluation criteria.
     *
     * @return list of question numbers
     */
    public List<Integer> getMissingCriteria() {
      return new ArrayList<>(missingCriteria);
    }

    /**
     * Builds a detailed error message based on validation errors.
     *
     * @return formatted error message string
     */
    public String buildErrorMessage() {
      List<String> messages = new ArrayList<>();

      if (!missingCriteria.isEmpty()) {
        String questionNumbers = missingCriteria.stream()
          .map(String::valueOf)
          .collect(Collectors.joining(", "));
        messages.add("Veuillez renseigner les critères d'évaluation des questions : " + questionNumbers);
      }

      if (!missingTextAnswers.isEmpty()) {
        String questionNumbers = missingTextAnswers.stream()
          .map(String::valueOf)
          .collect(Collectors.joining(", "));
        messages.add("Veuillez renseigner les réponses aux questions : " + questionNumbers);
      }

      return String.join("\n", messages);
    }
  }
}
