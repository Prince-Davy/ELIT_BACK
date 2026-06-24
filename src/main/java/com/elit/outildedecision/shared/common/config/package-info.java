/**
 * Common application configuration.
 *
 * This package contains shared configuration classes (e.g., HTTP clients,
 * serialization settings, and cross-cutting components) used across the application.
 *
 * Notes:
 * - Define reusable Spring beans only; no business logic.
 * - Keep configuration idempotent and side-effect free.
 * - Centralize defaults such as timeouts, codecs, and filters for HTTP clients.
 */
package com.elit.outildedecision.shared.common.config;
