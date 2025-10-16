
/**
 * This package provides the core components for applying scoring strategies
 * and enriching audit results based on different methodologies.
 * Classes overview:
 * - AuditCategoryResultEnricher: Responsible for enriching audit category results
 *   with additional computed data such as scores or comments.
 * - MethodsScoringStrategy: Interface defining the contract for scoring strategies
 *   applied to different audit methodologies.
 * - ScoringStrategyFactory: Factory class that creates appropriate scoring strategy
 *   instances based on the selected methodology type.
 * - ScrumScoringStrategy: Concrete implementation of MethodsScoringStrategy for
 *   the SCRUM methodology.
 * Design principles:
 * - Strategy Pattern: Each methodology has its own scoring strategy implementation,
 *   allowing flexible and extensible scoring logic.
 * - Factory Pattern: ScoringStrategyFactory centralizes the creation of strategy
 *   instances, promoting loose coupling and adherence to the Open/Closed Principle.
 * Usage:
 * These classes are used during audit processing to calculate scores for categories
 * and global evaluations, depending on the methodology (e.g., SCRUM, VMODEL).
 * Author: Mohamed Leulmi
 * Since: 2025-10-09
 */

package com.elit.outildedecision.audit.calculate;
