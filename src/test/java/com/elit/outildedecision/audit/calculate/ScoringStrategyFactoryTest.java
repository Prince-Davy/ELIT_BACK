package com.elit.outildedecision.audit.calculate;

import com.elit.outildedecision.util.MethodType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.Assert.*;

@ExtendWith(MockitoExtension.class)
public class ScoringStrategyFactoryTest {

  @Mock
  private AuditCategoryResultEnricher auditCategoryResultEnricher;

  private ScrumScoringStrategy scrumScoringStrategy;
  private ScoringStrategyFactory scoringStrategyFactory;

  @BeforeEach
  public void setUp() {
    scrumScoringStrategy = new ScrumScoringStrategy(auditCategoryResultEnricher);
    scoringStrategyFactory = new ScoringStrategyFactory(List.of(scrumScoringStrategy));
  }

  @Test
  public void shouldResolveCorrectStrategy() {
    MethodsScoringStrategy resolved = scoringStrategyFactory.resolveStrategyByMethodType(MethodType.SCRUM);
    Assertions.assertEquals(scrumScoringStrategy, resolved);
  }


  @Test
  public void shouldThrowExceptionForUnsupportedType() {
    MethodType unsupportedType = MethodType.KANBAN;
    IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> scoringStrategyFactory.resolveStrategyByMethodType(unsupportedType)
    );
    Assertions.assertTrue(exception.getMessage().contains("Unsupported method type"));
  }

  @Test
  public void shouldThrowExceptionForNullMethodType() {
    assertThrows(NullPointerException.class, () -> scoringStrategyFactory.resolveStrategyByMethodType(null));
  }

  @Test
  public void shouldThrowExceptionWhenStrategiesListIsNull() {
    assertThrows(NullPointerException.class, () -> new ScoringStrategyFactory(null));
  }
}

