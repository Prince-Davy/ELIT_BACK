package com.elit.outildedecision.collaboration.predictibility.repository;

import com.elit.outildedecision.collaboration.predictibility.entity.PredictibilityAnalysis;
import com.elit.outildedecision.collaboration.multicriteria.repository.MultiCriteriaAnalysisRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
class PredictibilityAnalysisRepositoryTest {

  @Autowired
  private PredictibilityAnalysisRepository predictibilityAnalysisRepository;

  @Autowired
  private MultiCriteriaAnalysisRepository multiCriteriaAnalysisRepository;


  @Test
  void testFindByLastIterationAndMultiCriteriaId_NoResult() {
    PredictibilityAnalysis result = predictibilityAnalysisRepository.findByLastIterationAndMultiCriteriaId(999L);

    assertThat(result).isNull();
  }
}
