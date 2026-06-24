/**
 * Startup data seeding.
 *
 * <p>Holds the idempotent seeding components run once at application startup on every environment:
 * {@link com.elit.outildedecision.shared.config.seed.DataSeedInitializer} orchestrates the registered
 * {@link com.elit.outildedecision.shared.config.seed.DataSeeder}s (e.g.
 * {@link com.elit.outildedecision.shared.config.seed.DemoDataSeeder}). Replaces the removed Liquibase seed
 * scripts.</p>
 */
package com.elit.outildedecision.shared.config.seed;
