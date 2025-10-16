/**
 * Audit services implementation.
 * This package contains service implementations for the audit domain.
 * Services encapsulate business use cases, orchestrate repository calls, and expose
 * operations to web/controllers.
 * Guidelines:
 * - Keep services stateless when possible.
 * - Validate inputs and throw domain-specific exceptions.
 * - Do not include persistence logic; delegate to repositories.
 * - Keep transaction boundaries clear and focused.
 */
package com.elit.outildedecision.audit.service.impl;
