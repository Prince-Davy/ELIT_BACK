package com.elit.outildedecision.shared.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.util.List;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

/**
 * Loads JSON resources into typed lists with graceful degradation.
 *
 * <p>A fresh {@link ObjectMapper} is created per call so that callers may register custom modules
 * without ever mutating the shared Spring-managed mapper. On any read failure the configured
 * fallback is returned and the error is logged, so a missing or malformed file degrades the feature
 * instead of crashing application startup.
 */
@Slf4j
@Component
public class JsonResourceLoader {

  private final ResourceLoader resourceLoader;

  /**
   * Constructor.
   *
   * @param resourceLoader the Spring resource loader used to resolve the location
   */
  public JsonResourceLoader(final ResourceLoader resourceLoader) {
    this.resourceLoader = resourceLoader;
  }

  /**
   * Reads a JSON resource into a list, returning the fallback on any failure.
   *
   * @param location   the resource location (e.g. {@code classpath:...})
   * @param type       the target list type reference
   * @param configurer optional mapper customisation (e.g. registering a module); may be {@code null}
   * @param fallback   the value returned when the resource cannot be read
   * @param <T>        the element type
   * @return the parsed list, or {@code fallback} on failure
   */
  public <T> List<T> loadList(final String location, final TypeReference<List<T>> type,
                              final Consumer<ObjectMapper> configurer, final List<T> fallback) {
    try (InputStream is = resourceLoader.getResource(location).getInputStream()) {
      final ObjectMapper mapper = new ObjectMapper();
      if (configurer != null) {
        configurer.accept(mapper);
      }
      final List<T> values = mapper.readValue(is, type);
      log.info("Loaded JSON resource {} ({} elements)", location, values.size());
      return values;
    } catch (Exception e) {
      log.error("Cannot read JSON resource {}, continuing with fallback", location, e);
      return fallback;
    }
  }
}
