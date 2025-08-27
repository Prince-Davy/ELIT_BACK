package com.elit.outildedecision.ai.answer.dto;

import com.elit.outildedecision.ai.answer.entity.AiAnswer;
import com.elit.outildedecision.entity.Project;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AiAnswerDTO {

  @NotNull
  private Map<@NotBlank @Size(max = 255) String, @NotBlank @Size(max = 1000) String> data;

  /**
   * Converts a list of AiAnswer entities to an AiAnswerDTO.
   * Each entity's question and answer are added to the DTO's data map.
   *
   * @param answers List of AiAnswer entities
   * @return AiAnswerDTO containing the mapped data
   */
  public static AiAnswerDTO fromEntityList(final List<AiAnswer> answers) {
    Map<String, String> data = new HashMap<>();
    for (AiAnswer answer : answers) {
      data.put(answer.getQuestion(), answer.getAnswer());
    }
    AiAnswerDTO dto = new AiAnswerDTO();
    dto.setData(data);
    return dto;
  }

  /**
   * Converts the DTO's data map into a list of AiAnswer entities.
   * Each entry in the map becomes an AiAnswer entity associated with the given project.
   *
   * @param project The project to associate with each AiAnswer
   * @return List of AiAnswer entities
   */
  public List<AiAnswer> toEntityList(final Project project) {
    List<AiAnswer> entities = new ArrayList<>();
    for (Map.Entry<String, String> entry : data.entrySet()) {
      AiAnswer answer = new AiAnswer();
      answer.setProject(project);
      answer.setQuestion(entry.getKey());
      answer.setAnswer(entry.getValue());
      entities.add(answer);
    }
    return entities;
  }
}
