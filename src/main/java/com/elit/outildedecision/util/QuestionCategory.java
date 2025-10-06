package com.elit.outildedecision.util;

/**
 * Enumeration representing the different categories of questions in an audit
 * questionnaire.
 * Following Single Responsibility Principle - this enum is solely responsible
 * for categorizing questions.
 */
public enum QuestionCategory {

  /**
   * Questions related to organizational structure and management.
   */
  ORGANISATIONS("Organisations"),

  /**
   * Questions about project objectives and scope definition.
   */
  OBJECTIF_ET_PERIMETRES("Objectif & périmètres"),

  /**
   * Questions concerning planning and resource allocation.
   */
  PLANIFICATION_ET_RESSOURCES("Planification & ressources"),

  /**
   * Questions about risk monitoring and change management.
   */
  SUIVI_RISQUE_ET_CHANGEMENT("Suivi Risque & changement"),

  /**
   * Questions related to tools and documentation practices.
   */
  OUTILS_ET_DOCUMENTATION("Outils & documentation"),

  /**
   * Questions about communication and collaboration processes.
   */
  COMMUNICATION_ET_COLLABORATION("Communication et collaboration");

  private final String displayName;

  /**
   * Constructor for QuestionCategory enum.
   *
   * @param displayName the human-readable display name
   */
  QuestionCategory(final String displayName) {
    this.displayName = displayName;
  }

  /**
   * Gets the display name of the category.
   *
   * @return the display name
   */
  public String getDisplayName() {
    return displayName;
  }

  /**
   * get the question Category .
   * @param category cateogry name
   * @return the Question Category
   */
  public static QuestionCategory getQuestionCategory(final String category) {
    for (QuestionCategory questionCategory : values()) {
      if (questionCategory.getDisplayName().equalsIgnoreCase(category)) {
        return questionCategory;
      }
    }
    throw new IllegalArgumentException("Invalid category: " + category);
  }
}
