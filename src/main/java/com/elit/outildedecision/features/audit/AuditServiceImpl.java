package com.elit.outildedecision.features.audit;

import com.elit.outildedecision.entity.Project;
import com.elit.outildedecision.entity.User;
import com.elit.outildedecision.features.audit.question.Question;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.exception.UserNotFoundException;
import com.elit.outildedecision.repository.ProjectRepository;
import com.elit.outildedecision.repository.UserRepository;
import com.elit.outildedecision.features.audit.question.QuestionRepository;
import com.elit.outildedecision.features.audit.question.QuestionRequestModel;
import com.elit.outildedecision.features.audit.question.QuestionResponseModel;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for Audit operations.
 */
@Service
public class AuditServiceImpl implements AuditService {

  private final AuditRepository auditRepository;
  private final ProjectRepository projectRepository;
  private final UserRepository userRepository;
  private final QuestionRepository questionRepository;

  /**
   * Constructor for AuditServiceImpl.
   * Following Dependency Inversion Principle - depends on abstractions.
   *
   * @param auditRepository    the audit repository
   * @param projectRepository  the project repository
   * @param userRepository     the user repository
   * @param questionRepository the question repository
   */
  public AuditServiceImpl(final AuditRepository auditRepository,
      final ProjectRepository projectRepository,
      final UserRepository userRepository,
      final QuestionRepository questionRepository) {
    this.auditRepository = auditRepository;
    this.projectRepository = projectRepository;
    this.userRepository = userRepository;
    this.questionRepository = questionRepository;
  }
  /**
   * Create a new audit.
   * @param auditRequest the audit request model
   * @param userEmail    the user email
   * @return the created audit response
   * @throws ProjectNotFoundException if project not found
   * @throws UserNotFoundException    if user not found
   */
  @Override
  public ResponseEntity<AuditResponseModel> createAudit(final AuditRequestModel auditRequest,
      final String userEmail)
      throws ProjectNotFoundException, UserNotFoundException {

    // Validate project exists
    Project project = projectRepository.findById(auditRequest.getProjectId())
        .orElseThrow(() -> new ProjectNotFoundException("Ce Projet n'existe pas!"));

    // Validate user exists
    User user = userRepository.findByUserEmail(userEmail);
    if (user == null) {
      throw new UserNotFoundException("Utilisateur non trouvé");
    }

    // Create audit entity
    Audit audit = Audit.builder()
        .method(auditRequest.getMethod())
        .project(project)
        .user(user)
        .build();

    // Save audit first to get the ID for questions
    Audit savedAudit = auditRepository.save(audit);

    // Create and save questions if provided
    if (auditRequest.getQuestions() != null && !auditRequest.getQuestions().isEmpty()) {
      List<Question> questions = auditRequest.getQuestions().stream()
          .map(questionRequest -> mapToQuestionEntity(questionRequest, savedAudit))
          .collect(Collectors.toList());

      questionRepository.saveAll(questions);
      savedAudit.getQuestions().addAll(questions);
    }

    // Build response
    AuditResponseModel response = mapToResponseModel(savedAudit);

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }
  /**
   * Get audit by project.
   * @param projectId project ID
   * @return list of audit response model
   * @throws ProjectNotFoundException if project not found
   */
  @Override
  public List<AuditResponseModel> getAuditsByProject(final Long projectId)
      throws ProjectNotFoundException {

    // Validate project exists
    if (!projectRepository.existsById(projectId)) {
      throw new ProjectNotFoundException("Ce Projet n'existe pas!");
    }

    List<Audit> audits = auditRepository.findByProjectProjectIdOrderByCreatedAtDesc(projectId);

    return audits.stream()
        .map(this::mapToResponseModel)
        .collect(Collectors.toList());
  }
  /**
   * Get audit by project paginated.
   * @param projectId project ID
   * @param page oage
   * @param size size
   * @return list of audit response model
   * @throws ProjectNotFoundException if project not found
   */
  @Override
  public Page<AuditResponseModel> getAuditsByProjectPaginated(final Long projectId,
      final int page,
      final int size)
      throws ProjectNotFoundException {

    // Validate project exists
    if (!projectRepository.existsById(projectId)) {
      throw new ProjectNotFoundException("Ce Projet n'existe pas!");
    }

    Pageable pageable = PageRequest.of(page, size);
    Page<Audit> auditPage = auditRepository.findByProjectProjectIdOrderByCreatedAtDesc(projectId, pageable);

    return auditPage.map(this::mapToResponseModel);
  }

  /**
   * Map Audit entity to AuditResponseModel.
   * Following Single Responsibility Principle - handles only audit mapping.
   *
   * @param audit the audit entity
   * @return the audit response model
   */
  private AuditResponseModel mapToResponseModel(final Audit audit) {
    // Get user role from project permissions (simplified - takes first permission
    // found)
    String userRole = "Unknown";
    if (audit.getUser().getUserProjectPermissions() != null && !audit.getUser().getUserProjectPermissions().isEmpty()) {
      userRole = audit.getUser().getUserProjectPermissions().get(0).getPermission();
    }

    // Map questions to response models
    List<QuestionResponseModel> questionResponses = new ArrayList<>();
    if (audit.getQuestions() != null) {
      questionResponses = audit.getQuestions().stream()
          .map(question -> mapToQuestionResponseModel(question))
          .collect(Collectors.toList());
    }

    AuditResponseModel response = AuditResponseModel.builder()
        .totalAuditCount(auditRepository.countAuditsByProjectId(audit.getProject().getProjectId()))
        .auditId(audit.getAuditId())
        .method(audit.getMethod())
        .auditLaunchDate(audit.getAuditLaunchDate())
        .projectId(audit.getProject().getProjectId())
        .projectName(audit.getProject().getName())
        .userId(audit.getUser().getUserId())
        .userFirstName(audit.getUser().getUserFirstName())
        .userLastName(audit.getUser().getUserLastName())
        .userRole(userRole)
        .questions(questionResponses)
        .build();

    // Calculate question metrics
    response.calculateQuestionMetrics();

    return response;
  }

  /**
   * Map QuestionRequestModel to Question entity.
   * Following Single Responsibility Principle - handles only question request
   * mapping.
   * Includes automatic score calculation and history creation.
   *
   * @param questionRequest the question request model
   * @param audit           the associated audit entity
   * @return the question entity
   */
  private Question mapToQuestionEntity(final QuestionRequestModel questionRequest, final Audit audit) {
    Question question = Question.builder()
        .category(questionRequest.getCategory())
        .questionRef(questionRequest.getQuestionRef())
        .weight(questionRequest.getWeight())
        .auditAnswerLabel(questionRequest.getAuditAnswerLabel())
        .auditAnswerValue(questionRequest.getAuditAnswerValue())
        .audit(audit)
        .build();

    // Calculate score automatically based on answer and type
    question.calculateAndSetScore();

    return question;
  }

  /**
   * Map Question entity to QuestionResponseModel.
   * Following Single Responsibility Principle - handles only question response
   * mapping.
   * Includes complete scoring metrics and answer history.
   *
   * @param question the question entity
   * @return the question response model
   */
  private QuestionResponseModel mapToQuestionResponseModel(final Question question) {
    QuestionResponseModel response = QuestionResponseModel.builder()
        .questionRef(question.getQuestionRef())
        .weight(question.getWeight())
        .auditAnswerLabel(question.getAuditAnswerLabel())
        .auditAnswerValue(question.getAuditAnswerValue())
        .currentScore(question.getCurrentScore())
        .isAnswered(question.isAnswered())
        .build();

    // Set category with automatic display name population
    response.setCategory(question.getCategory()); // Set answer type with automatic display name population

    // Calculate all score metrics automatically
    response.calculateScoreMetrics();


    return response;
  }

}
