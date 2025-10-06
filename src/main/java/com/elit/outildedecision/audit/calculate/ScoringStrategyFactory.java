package com.elit.outildedecision.audit.calculate;

import com.elit.outildedecision.util.MethodType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.EnumMap;
import java.util.stream.Collectors;

@Component
public final class ScoringStrategyFactory {

  private final Map<MethodType, MethodsScoringStrategy> registry;


  /**
   * Constructor that allows manual injection of scoring strategies.
   *
   * @param strategies A collection of {@link MethodsScoringStrategy} implementations to be registered.
   *                   Must not be {@code null}.
   * @throws NullPointerException if {@code strategies} is {@code null}.
   */
  @Autowired
  public ScoringStrategyFactory(final List<MethodsScoringStrategy> strategies) {
    Objects.requireNonNull(strategies, "strategies must not be null");
    this.registry = buildRegistry(strategies);
  }


  /**
   * Resolves and returns the scoring strategy associated with the given method type.
   *
   * @param methodType The {@link MethodType} for which the strategy is requested. Must not be {@code null}.
   * @return The corresponding {@link MethodsScoringStrategy} for the given method type.
   * @throws NullPointerException if {@code methodType} is {@code null}.
   * @throws IllegalArgumentException if no strategy is registered for the given method type.
   */
  public MethodsScoringStrategy resolveStrategyByMethodType(final MethodType methodType) {
    Objects.requireNonNull(methodType, "methodType must not be null");
    MethodsScoringStrategy strategy = registry.get(methodType);
    if (strategy == null) {
      String supported = registry.keySet().stream()
        .map(Enum::name)
        .sorted()
        .collect(Collectors.joining(", "));
      throw new IllegalArgumentException("Unsupported method type: " + methodType + " (supported: " + supported + ")");
    }
    return strategy;
  }

  // ----------------- Méthodes privées -----------------

  /**
   * Builds a registry map from the provided collection of scoring strategies.
   *
   * @param strategies A collection of {@link MethodsScoringStrategy} implementations.
   * @return An unmodifiable map of {@link MethodType} to {@link MethodsScoringStrategy}.
   */
  private static Map<MethodType, MethodsScoringStrategy> buildRegistry(final Collection<? extends MethodsScoringStrategy> strategies) {
    EnumMap<MethodType, MethodsScoringStrategy> map = new EnumMap<>(MethodType.class);
    for (MethodsScoringStrategy strategy : strategies) {
      register(map, strategy);
    }
    return Collections.unmodifiableMap(map);
  }


  /**
   * Registers a single scoring strategy into the provided map.
   *
   * @param map The map where the strategy should be registered.
   * @param strategy The {@link MethodsScoringStrategy} to register. Must not be {@code null}.
   * @throws NullPointerException if {@code strategy} or its supported type is {@code null}.
   * @throws IllegalStateException if a strategy for the same method type is already registered.
   */
  private static void register(final Map<MethodType, MethodsScoringStrategy> map, final MethodsScoringStrategy strategy) {
    Objects.requireNonNull(strategy, "strategy must not be null");
    MethodType type = Objects.requireNonNull(strategy.supportedType(), "supportedType is null");
    if (map.putIfAbsent(type, strategy) != null) {
      throw new IllegalStateException("Duplicate strategy for " + type);
    }
  }
}
