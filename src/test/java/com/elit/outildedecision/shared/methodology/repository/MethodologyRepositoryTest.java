package com.elit.outildedecision.shared.methodology.repository;

import com.elit.outildedecision.shared.methodology.entity.Methodology;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class MethodologyRepositoryTest {

  @Autowired
  private MethodologyRepository methodologyRepository;

  private Methodology methodology;

  @BeforeEach
  public void setUp() {
    methodology = Methodology.builder()
      .name("Agile")
      .description("A flexible and iterative approach to project management.")
      .advantages("Quick adaptation to changes, High customer satisfaction")
      .disadvantages("Requires high client involvement, May lead to scope creep")
      .build();

    methodologyRepository.save(methodology);
  }

  @Test
  void testSaveMethodology() {
    assertThat(methodology).isNotNull();
    assertThat(methodology.getMethodologyId()).isNotNull();
    assertThat(methodology.getName()).isEqualTo("Agile");
    assertThat(methodology.getDescription()).isEqualTo("A flexible and iterative approach to project management.");
    assertThat(methodology.getAdvantages()).isEqualTo("Quick adaptation to changes, High customer satisfaction");
    assertThat(methodology.getDisadvantages()).isEqualTo("Requires high client involvement, May lead to scope creep");
  }

  @Test
  void testFindByNameNotFound() {
    Methodology foundMethodology = methodologyRepository.findByName("NonExistentMethodology");

    assertThat(foundMethodology).isNull();
  }
}
