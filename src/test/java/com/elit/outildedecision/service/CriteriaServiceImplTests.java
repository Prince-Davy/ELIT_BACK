package com.elit.outildedecision.service;
import com.elit.outildedecision.entity.Criteria;
import com.elit.outildedecision.entity.FormQuestion;
import com.elit.outildedecision.repository.CriteriaRepository;
import com.elit.outildedecision.service.implementation.CriteriaServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CriteriaServiceImplTests {

  @Mock
  private CriteriaRepository criteriaRepository;
  @InjectMocks
  private CriteriaServiceImpl criteriaService;
  Set<Criteria> criterias= new HashSet<>();
  FormQuestion formQuestion;
  Criteria criteriaOne;
  Criteria criteriaTwo;


  @BeforeEach
  void setUp() {
    formQuestion = new FormQuestion();
    criteriaOne = new Criteria();
    criteriaOne.setCriteriaId(1L);
    criteriaOne.setCriteriaName("criteriaOne");
    criteriaOne.setFormQuestion(formQuestion);
    criteriaOne.setCriteriaValue(0);

    criteriaTwo = new Criteria();
    criteriaTwo.setCriteriaId(1L);
    criteriaTwo.setCriteriaName("criteriaTwo");
    criteriaTwo.setFormQuestion(formQuestion);
    criteriaTwo.setCriteriaValue(0);
  }

  @Test
  void testCreateCriterias() {
    Set<Criteria> persistedCriterias = new HashSet<>();
    assertEquals(persistedCriterias,criteriaService.createCriterias(criterias,formQuestion));
  }

  @Test
  void testCreateCriteriasNotEmpty() {
    criterias.add(criteriaOne);
    Set<Criteria> persistedCriterias = new HashSet<>();
    criterias.add(criteriaOne);
    Criteria criteriaToPersist =
      Criteria.builder()
        .criteriaName(criteriaOne.getCriteriaName())
        .formQuestion(formQuestion)
        .criteriaValue(0)
        .build();
    when(criteriaRepository.save(criteriaToPersist)).thenReturn(criteriaToPersist);
    persistedCriterias.add(criteriaToPersist);
    assertEquals(persistedCriterias,criteriaService.createCriterias(criterias,formQuestion));
  }


  @Test
  void testUpdateCriteria_Success() {
    when(criteriaRepository.findById(anyLong())).thenReturn(Optional.of(criteriaOne));
    when(criteriaRepository.save(criteriaOne)).thenReturn(criteriaOne);
    assertEquals(criteriaOne,criteriaService.updateCriteria(criteriaOne));
  }

  @Test
  void testUpdateCriteria_throwNoSuchElement() {
    when(criteriaRepository.findById(anyLong())).thenReturn(Optional.empty());
    NoSuchElementException p = Assertions.assertThrows(NoSuchElementException.class, ()-> criteriaService.updateCriteria(criteriaOne));
    Assertions.assertEquals("Criteria not found with ID: {}1", p.getMessage());
  }


}
