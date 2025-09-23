package com.elit.outildedecision.audit.calculate;

import com.elit.outildedecision.util.MethodType;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.util.List;

import static org.junit.Assert.assertThrows;

public class ScoringStrategyFactoryTest {

  @Test
  public void shouldResolveCorrectStrategy() {
    ScrumScoringStrategy scrumScoringStrategy = new ScrumScoringStrategy();
    ScoringStrategyFactory scoringStrategyFactory = new ScoringStrategyFactory(List.of(scrumScoringStrategy));
    MethodsScoringStrategy resolved = scoringStrategyFactory.resolveStrategyByMethodType(MethodType.SCRUM);

    Assertions.assertEquals(scrumScoringStrategy, resolved);
  }


  @Test
  public void shouldThrowExceptionForUnsupportedType() {
    ScoringStrategyFactory factory = new ScoringStrategyFactory(List.of(new ScrumScoringStrategy()));

    assertThrows(IllegalArgumentException.class, () -> factory.resolveStrategyByMethodType(MethodType.KANBAN));
  }

}
