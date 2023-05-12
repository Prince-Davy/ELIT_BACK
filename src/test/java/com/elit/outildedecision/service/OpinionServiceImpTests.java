package com.elit.outildedecision.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;

import com.elit.outildedecision.entity.Opinion;
import com.elit.outildedecision.entity.User;
import com.elit.outildedecision.exception.OpinionAlreadyExistException;
import com.elit.outildedecision.repository.OpinionRepository;
import com.elit.outildedecision.repository.UserRepository;
import com.elit.outildedecision.service.implementation.OpinionServiceImpl;
import com.elit.outildedecision.util.ModelSamples;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
@ActiveProfiles("test")
class OpinionServiceImpTests {
    @Mock
    private UserRepository userRepository;

    @Mock
    private OpinionRepository opinionRepository;

    @InjectMocks
    private OpinionServiceImpl opinionServiceImpl;

  @Test
  @WithMockUser(username = "user@user.com")
  void createOpinionSucces() throws OpinionAlreadyExistException {
    User user = ModelSamples.UserSamples.getUser();
    Opinion opinion = ModelSamples.OpinionSamples.getOpinion();

    when(userRepository.findByUserEmail(any())).thenReturn(user);
    when(opinionRepository.findByUserId(any())).thenReturn(null);
    when(opinionRepository.save(opinion)).thenReturn(opinion);

    Assertions.assertEquals(ResponseEntity.ok().body("L'avis a bien été enregistré."), opinionServiceImpl.createOpinion(ModelSamples.OpinionSamples.getOpinionModel()));
  }

  @Test
  @WithMockUser(username = "user@user.com")
  void createOpinionException()  {
    User user = ModelSamples.UserSamples.getUser();
    Opinion opinion = ModelSamples.OpinionSamples.getOpinion();

    when(userRepository.findByUserEmail(any())).thenReturn(user);
    when(opinionRepository.findByUserId(any())).thenReturn(opinion);
    when(opinionRepository.save(opinion)).thenReturn(opinion);

    Assertions.assertThrows(OpinionAlreadyExistException.class, () -> opinionServiceImpl.createOpinion(ModelSamples.OpinionSamples.getOpinionModel()));
  }

  @Test
  @WithMockUser(username = "user@user.com")
  void createOpinionElse() throws OpinionAlreadyExistException {
    User user = ModelSamples.UserSamples.getUser();
    Opinion opinion = ModelSamples.OpinionSamples.getOpinion();
    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn("test@test.com");

    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(userRepository.findByUserEmail(any())).thenReturn(user);
    when(opinionRepository.findByUserId(any())).thenReturn(null);
    when(opinionRepository.save(opinion)).thenReturn(opinion);

    Assertions.assertEquals(ResponseEntity.ok().body("L'avis a bien été enregistré."), opinionServiceImpl.createOpinion(ModelSamples.OpinionSamples.getOpinionModel()));


  }

  @Test
  @WithMockUser(username = "user@user.com")
  void GetOpinionIsAlreadyExist_True() {
    User user = ModelSamples.UserSamples.getUser();
    Opinion opinion = ModelSamples.OpinionSamples.getOpinion();
    when(userRepository.findByUserEmail(any())).thenReturn(user);
    when(opinionRepository.findByUserId(any())).thenReturn(opinion);

    Assertions.assertTrue(opinionServiceImpl.getOpinionIsAlreadyExist());
  }

  @Test
  @WithMockUser(username = "user@user.com")
  void GetOpinionIsAlreadyExist_false() {
    User user = ModelSamples.UserSamples.getUser();
    when(userRepository.findByUserEmail(any())).thenReturn(user);
    when(opinionRepository.findByUserId(any())).thenReturn(null);

    Assertions.assertFalse(opinionServiceImpl.getOpinionIsAlreadyExist());
  }
}
