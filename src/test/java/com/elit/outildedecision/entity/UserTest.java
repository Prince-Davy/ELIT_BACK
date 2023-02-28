package com.elit.outildedecision.entity;

import com.elit.outildedecision.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertTrue;

public class UserTest {

  private Validator validator;

  private UserRepository userRepository;

  @InjectMocks
  private User user = new User();

  @BeforeEach
  public void setUp() {
    this.user = initUser();
  }

  @Test
  public void getUser() {
    List<User> users = this.userRepository.findAll();
    System.out.println(users);
  }

  @Test
  public void userIdNull() {
    User user = new User();
    Long id = null;
    user.setUserId(id);

    Set<ConstraintViolation<User>> violations = validator.validate(user);
    assertTrue(violations.isEmpty());
  }

  /***** UTILS *****/
  private User initUser() {
    final User user = new User();
    user.setUserId(1L);
    user.setIsEmailVerified(true);
    user.setUserEmail("test@test.com");
    this.userRepository.saveAndFlush(user);


    return user;
  }

}
