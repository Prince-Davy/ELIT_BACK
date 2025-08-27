package com.elit.outildedecision.ai.answer.service.implementation;

import com.elit.outildedecision.ai.answer.entity.AiAnswer;
import com.elit.outildedecision.ai.answer.exception.AiAnswerNotFoundException;
import com.elit.outildedecision.entity.Project;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.ai.answer.repository.AiAnswerRepository;
import com.elit.outildedecision.repository.ProjectRepository;
import com.elit.outildedecision.ai.answer.service.AiAnswerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Slf4j
public class AiAnswerServiceImpl implements AiAnswerService {

  private final AiAnswerRepository aiAnswerRepository;
  private final ProjectRepository projectRepository;


  /**
   * Constructs an instance of {@code AiAnswerServiceImpl} with the required repositories.
   *
   * @param aiAnswerRepository the repository used to manage AI answers
   * @param projectRepository the repository used to manage projects
   */
  public AiAnswerServiceImpl(final AiAnswerRepository aiAnswerRepository, final ProjectRepository projectRepository) {
    this.aiAnswerRepository = aiAnswerRepository;
    this.projectRepository = projectRepository;
  }

  /**
   * Saves AI-generated answers for a given project.
   * First deletes existing answers, then saves the new ones.
   *
   * @param projectId the ID of the project
   * @param data a map of question-answer pairs
   * @throws ProjectNotFoundException if the project does not exist
   * @throws AiAnswerNotFoundException if no valid answers are provided
   */
  @Override
  @Transactional
  public void saveAnswers(final Long projectId, final Map<String, String> data) throws ProjectNotFoundException, AiAnswerNotFoundException {
    Optional<Project> projectOpt = projectRepository.findById(projectId);
    if (projectOpt.isEmpty()) {
      log.error("Project not found for id {}", projectId);
      throw new ProjectNotFoundException("Project not found for id: " + projectId);
    }
    Project project = projectOpt.get();

    if (data == null || data.isEmpty()) {
      log.warn("No AI answers provided for project id {}", projectId);
      throw new AiAnswerNotFoundException("Aucune réponse IA fournie");
    }


    // Delete old answers
    aiAnswerRepository.deleteByProject_ProjectId(projectId);

    List<AiAnswer> answersToSave = data.entrySet().stream()
      .filter(entry -> entry.getKey() != null && !entry.getKey().trim().isEmpty())
      .filter(entry -> entry.getValue() != null && !entry.getValue().trim().isEmpty())
      .map(entry -> {
        AiAnswer aiAnswer = new AiAnswer();
        aiAnswer.setProject(project);
        aiAnswer.setQuestion(entry.getKey().trim());
        aiAnswer.setAnswer(entry.getValue().trim());
        return aiAnswer;
      })
      .collect(Collectors.toList());

    if (answersToSave.isEmpty()) {
      log.warn("No valid answers to save for project {}", projectId);
      throw new AiAnswerNotFoundException("No valid answers to save for project " + projectId);
    }

    aiAnswerRepository.saveAll(answersToSave);
    log.info("AI answers successfully saved for project {}", projectId);
  }

  /**
   * Retrieves all AI-generated answers for a given project.
   *
   * @param projectId the ID of the project
   * @return a list of AiAnswer entities
   * @throws AiAnswerNotFoundException if no answers are found
   */
  @Override
  public List<AiAnswer> getAnswersByProject(final Long projectId) throws AiAnswerNotFoundException {
    List<AiAnswer> answers = aiAnswerRepository.findByProject_ProjectId(projectId);
    if (answers == null || answers.isEmpty()) {
      log.warn("No AI answers found for project {}", projectId);
      throw new AiAnswerNotFoundException("No AI answers found for project " + projectId);
    }
    return answers;
  }
}
