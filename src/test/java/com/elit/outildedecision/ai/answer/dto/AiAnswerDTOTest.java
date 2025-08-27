package com.elit.outildedecision.ai.answer.dto;

import com.elit.outildedecision.ai.answer.entity.AiAnswer;
import com.elit.outildedecision.entity.Project;
import org.junit.jupiter.api.Test;


import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AiAnswerDTOTest {

  @Test
  void fromEntityList_convertitLesEntitesEnDTO() {
    // Arrange
    Project project = new Project();
    AiAnswer a1 = new AiAnswer();
    a1.setProject(project);
    a1.setQuestion("Q1");
    a1.setAnswer("A1");

    AiAnswer a2 = new AiAnswer();
    a2.setProject(project);
    a2.setQuestion("Q2");
    a2.setAnswer("A2");

    // Act
    AiAnswerDTO dto = AiAnswerDTO.fromEntityList(Arrays.asList(a1, a2));

    // Assert
    assertNotNull(dto);
    assertNotNull(dto.getData());
    assertEquals(2, dto.getData().size());
    assertEquals("A1", dto.getData().get("Q1"));
    assertEquals("A2", dto.getData().get("Q2"));
  }

  @Test
  void fromEntityList_listeVide_retourneDTOAvecMapVide() {
    // Act
    AiAnswerDTO dto = AiAnswerDTO.fromEntityList(Collections.emptyList());

    // Assert
    assertNotNull(dto);
    assertNotNull(dto.getData());
    assertTrue(dto.getData().isEmpty());
  }

  @Test
  void fromEntityList_clesDupliquees_derniereValeurConservee() {
    // Arrange
    Project project = new Project();

    AiAnswer a1 = new AiAnswer();
    a1.setProject(project);
    a1.setQuestion("Q1");
    a1.setAnswer("A1");

    AiAnswer a2 = new AiAnswer();
    a2.setProject(project);
    a2.setQuestion("Q1"); // même clé
    a2.setAnswer("A1bis"); // valeur plus récente

    // Act
    AiAnswerDTO dto = AiAnswerDTO.fromEntityList(Arrays.asList(a1, a2));

    // Assert
    assertNotNull(dto.getData());
    assertEquals(1, dto.getData().size());
    assertEquals("A1bis", dto.getData().get("Q1"));
  }

  @Test
  void toEntityList_convertitDTOEnEntites_etLieLeProjet() {
    // Arrange
    Project project = new Project();
    Map<String, String> data = new HashMap<>();
    data.put("Q1", "A1");
    data.put("Q2", "A2");
    AiAnswerDTO dto = new AiAnswerDTO(data);

    // Act
    List<AiAnswer> entities = dto.toEntityList(project);

    // Assert
    assertNotNull(entities);
    assertEquals(2, entities.size());

    // Vérifie le contenu sans supposer l'ordre de la Map
    Set<String> questions = new HashSet<>();
    Set<String> answers = new HashSet<>();
    for (AiAnswer e : entities) {
      assertSame(project, e.getProject());
      questions.add(e.getQuestion());
      answers.add(e.getAnswer());
    }
    assertTrue(questions.contains("Q1"));
    assertTrue(questions.contains("Q2"));
    assertTrue(answers.contains("A1"));
    assertTrue(answers.contains("A2"));
  }

  @Test
  void toEntityList_mapVide_retourneListeVide() {
    // Arrange
    Project project = new Project();
    AiAnswerDTO dto = new AiAnswerDTO(Collections.emptyMap());

    // Act
    List<AiAnswer> entities = dto.toEntityList(project);

    // Assert
    assertNotNull(entities);
    assertTrue(entities.isEmpty());
  }
}
