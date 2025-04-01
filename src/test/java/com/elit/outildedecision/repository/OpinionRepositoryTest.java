package com.elit.outildedecision.repository;

import com.elit.outildedecision.entity.Opinion;
import com.elit.outildedecision.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class OpinionRepositoryTest {

  @Autowired
  private OpinionRepository opinionRepository;

  @Autowired
  private UserRepository userRepository;

  private User testUser;

  @BeforeEach
  void setUp() {
    testUser = User.builder()
      .username("Test User")
      .userEmail("testuser@example.com")
      .build();

    userRepository.save(testUser);
  }

  @Test
  @Disabled
  void testFindByUserId_ReturnsOpinion() {
    Opinion opinion = Opinion.builder()
      .rating(5)
      .description("Excellent service.")
      .user(testUser)
      .build();

    opinionRepository.save(opinion);

    Opinion foundOpinion = opinionRepository.findByUserId(testUser.getUserId());

    assertThat(foundOpinion).isNotNull();
    assertThat(foundOpinion.getUser().getUserId()).isEqualTo(testUser.getUserId());
    assertThat(foundOpinion.getRating()).isEqualTo(5);
    assertThat(foundOpinion.getDescription()).isEqualTo("Excellent service.");
  }

  @Test
  @Disabled
  void testFindByUserId_ReturnsNull_WhenNoOpinionExists() {
    Opinion foundOpinion = opinionRepository.findByUserId(testUser.getUserId());

    assertThat(foundOpinion).isNull();
  }
}
