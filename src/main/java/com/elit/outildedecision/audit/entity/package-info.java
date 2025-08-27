/**
 * This package contains entity classes related to the audit functionality.
 *
 * It includes:
 * - {@code Audit}: Represents an audit session linked to a project and a user.
 * - {@code Question}: Represents individual questions within an audit, including scoring logic.
 *
 * These entities are designed to support both extensibility and flexibility:
 * - Methodology-agnostic design for audits.
 * - Scoring and weighting mechanisms for questions.
 * - Clear separation of concerns following SOLID principles.
 */
package com.elit.outildedecision.audit.entity;
