package com.elit.outildedecision.util;

/**
 * Enumération des types de modification pour l'historique des réponses.
 * Définit les différents types de changements possibles sur les réponses aux
 * questions d'audit.
 *
 * @author ELIT
 * @version 1.0
 */
public enum ModificationType {
  /**
   * Réponse initiale lors de la création.
   */
  INITIAL_ANSWER("Réponse initiale"),

  /**
   * Mise à jour/modification de la réponse.
   */
  ANSWER_UPDATE("Modification de réponse"),

  /**
   * Correction d'une erreur dans la réponse.
   */
  CORRECTION("Correction"),

  /**
   * Révision et validation de la réponse.
   */
  REVIEW("Révision");

  private final String displayName;

  ModificationType(final String displayName) {
    this.displayName = displayName;
  }

  /**
   * Retourne le nom d'affichage du type de modification.
   *
   * @return le nom d'affichage localisé
   */
  public String getDisplayName() {
    return displayName;
  }

  /**
   * Détermine si le type de modification est automatique.
   *
   * @return true si la modification est généralement automatique
   */
  public boolean isAutomaticType() {
    return this == INITIAL_ANSWER;
  }

  /**
   * Détermine si le type de modification nécessite une raison obligatoire.
   *
   * @return true si une raison est obligatoire pour ce type
   */
  public boolean requiresReason() {
    return this == CORRECTION || this == REVIEW;
  }
}
