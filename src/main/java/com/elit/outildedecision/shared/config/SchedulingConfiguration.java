package com.elit.outildedecision.shared.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Enables {@code @Scheduled} tasks (e.g. the periodic purge of expired pending accounts).
 *
 * <p>Each scheduled task can be disabled independently by setting its cron property to {@code "-"}.
 */
@Configuration
@EnableScheduling
public class SchedulingConfiguration {
}
