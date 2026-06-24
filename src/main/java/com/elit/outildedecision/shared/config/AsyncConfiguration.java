package com.elit.outildedecision.shared.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Enables {@code @Async} method execution so post-commit side effects (e.g. the registration
 * confirmation email) run off the request thread on Spring Boot's task executor.
 */
@Configuration
@EnableAsync
public class AsyncConfiguration {
}
