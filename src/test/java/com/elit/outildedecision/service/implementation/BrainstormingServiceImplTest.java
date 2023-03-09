package com.elit.outildedecision.service.implementation;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.elit.outildedecision.entity.Brainstorming;
import com.elit.outildedecision.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.*;

class BrainstormingServiceImplTest {

  @Mock
  BrainstormingRepository brainstormingRepository;
  @Mock
  FormQuestionRepository formQuestionRepository;
  @Mock
  ProjectUserPermissionRepository projectUserPermissionRepository;
  @Mock
  UserRepository userRepository;
  @Mock
  ProjectRepository projectRepository;
  @Mock
  MultiCriteriaAnalysisRepository multiCriteriaAnalysisRepository;
  @Mock
  PredictibilityAnalysisRepository predictibilityAnalysisRepository;
  @InjectMocks
  BrainstormingServiceImpl brainstormingService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }
  @Test
  void testValidateOrRejectBrainstorming() throws Exception {

    // Create a Brainstorming object
    Brainstorming brainstorming = new Brainstorming();
    brainstorming.setBrainstormingId(1L);
    brainstorming.setIsChecked(false);

    // Set up the repository to return the Brainstorming object when findById is called
    when(brainstormingRepository.findById(1L)).thenReturn(Optional.of(brainstorming));

    // Create an HttpEntity containing a JSON object with a "isConfirmed" field set to true
    String json = "{\"isConfirmed\": true}";
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> entity = new HttpEntity<>(json, headers);

    // Call the method under test
    ResponseEntity<String> response = brainstormingService.validateOrRejectBrainstorming(1L, entity);

    // Assert that the Brainstorming object was updated correctly
    assertEquals(true, brainstorming.getIsChecked());

    // Assert that the response is as expected
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Le commentaire a bien été enregistré.", response.getBody());
  }

}
