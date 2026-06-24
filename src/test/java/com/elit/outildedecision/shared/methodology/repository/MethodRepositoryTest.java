package com.elit.outildedecision.shared.methodology.repository;

import com.elit.outildedecision.shared.methodology.entity.Method;
import com.elit.outildedecision.shared.methodology.entity.Methodology;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class MethodRepositoryTest {

  @Autowired
  private MethodRepository methodRepository;

  @Autowired
  private MethodologyRepository methodologyRepository;

  private Methodology methodology;

  @BeforeEach
  void setUp() {
    methodology = Methodology.builder()
      .name("Agile")
      .description("A flexible and iterative approach to project management.")
      .build();

    methodology = methodologyRepository.save(methodology);

    Method method1 = Method.builder()
      .name("Scrum")
      .description("An Agile framework for managing work.")
      .methodology(methodology)
      .build();

    Method method2 = Method.builder()
      .name("Kanban")
      .description("A lean method to manage and improve work across human systems.")
      .methodology(methodology)
      .build();

    methodRepository.save(method1);
    methodRepository.save(method2);
  }

  @Test
  void testFindAllMethodsByMethodologyId() {
    List<Method> methods = methodRepository.findAllMethodsByMethodologyId(methodology.getMethodologyId());

    assertThat(methods).isNotEmpty();
    assertThat(methods).hasSize(2);

    assertThat(methods.get(0).getName()).isEqualTo("Scrum");
    assertThat(methods.get(0).getMethodology().getName()).isEqualTo("Agile");

    assertThat(methods.get(1).getName()).isEqualTo("Kanban");
    assertThat(methods.get(1).getMethodology().getName()).isEqualTo("Agile");
  }

  @Test
  void testFindAllMethodsByMethodologyId_NoMethods() {
    List<Method> methods = methodRepository.findAllMethodsByMethodologyId(999L);

    assertThat(methods).isEmpty();
  }
}
