/**
 * This package contains JPA entities representing comments used in audits and evaluations,
 * organized by methodology type.
 * The design follows an inheritance strategy to ensure reusability and consistency:
 * - AbstractComment: Base abstract class defining common properties such as ID, evaluation level,
 *   comment text, and method type.
 * - CategoryComment: Abstract entity for comments associated with a specific question category.
 * - GlobalComment: Abstract entity for global comments linked to a methodology and evaluation level.
 * - ScrumCategoryComment and ScrumGlobalComment: Concrete implementations for the SCRUM methodology.
 * - VModelCategoryComment and VModelGlobalComment: Concrete implementations for the VMODEL methodology.
 * Design principles:
 * - SINGLE_TABLE inheritance: Entities share a single table with a discriminator column (method_type)
 *   to differentiate subclasses.
 * - Enum mapping: Evaluation levels and categories are stored as strings using EnumType.STRING.
 * - Unique constraints:
 *   * category_comment: (method_type, category, level)
 *   * global_comment: (method_type, level)
 * Usage:
 * These entities are used to store qualitative comments for each evaluation level, differentiated
 * by methodology (SCRUM, VMODEL, etc.), and are retrieved through Spring Data JPA repositories.
 * Author: Mohamed Leulmi
 * Since: 2025-10-09
 */

package com.elit.outildedecision.audit.entity.comment;
