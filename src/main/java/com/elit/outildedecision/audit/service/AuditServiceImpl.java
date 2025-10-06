package com.elit.outildedecision.audit.service;

import com.elit.outildedecision.audit.calculate.MethodsScoringStrategy;
import com.elit.outildedecision.audit.calculate.ScoringStrategyFactory;

import com.elit.outildedecision.audit.entity.Audit;
import com.elit.outildedecision.audit.entity.AuditScore;
import com.elit.outildedecision.audit.entity.CategoryComment;
import com.elit.outildedecision.audit.entity.LevelScore;
import com.elit.outildedecision.audit.entity.GlobalComment;
import com.elit.outildedecision.audit.entity.Question;
import com.elit.outildedecision.audit.mapper.AuditMapper;
import com.elit.outildedecision.audit.model.AuditRequestModel;
import com.elit.outildedecision.audit.model.AuditResponseModel;
import com.elit.outildedecision.audit.model.QuestionRequestModel;
import com.elit.outildedecision.audit.model.QuestionResponseModel;
import com.elit.outildedecision.audit.model.AuditScoreDTO;
import com.elit.outildedecision.audit.repository.AuditRepository;
import com.elit.outildedecision.entity.Project;
import com.elit.outildedecision.entity.User;
import com.elit.outildedecision.exception.AuditNotFoundException;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.exception.UserNotFoundException;
import com.elit.outildedecision.repository.ProjectRepository;
import com.elit.outildedecision.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Service implementation for Audit operations.
 */
@Service
public class AuditServiceImpl implements AuditService {

  private final AuditRepository auditRepository;
  private final ProjectRepository projectRepository;
  private final UserRepository userRepository;
  private final ScoringStrategyFactory scoringStrategyFactory;
  private final AuditMapper  auditMapper;
  private final CategoryCommentService categoryCommentService;
  private final GlobalCommentService globalCommentService;


  /**
   * Constructor for AuditServiceImpl.
   * Follows the Dependency Inversion Principle by depending on abstractions.
   *
   * @param auditRepository the repository for managing audit entities
   * @param projectRepository the repository for managing project entities
   * @param userRepository the repository for managing user entities
   * @param scoringStrategyFactory the factory for creating scoring strategies
   * @param auditMapper the mapper for converting between audit models and entities
   * @param categoryCommentService the service for retrieving category comments
   * @param globalCommentService the service for retreiving global comments
   */
  public AuditServiceImpl(final AuditRepository auditRepository,
                          final ProjectRepository projectRepository,
                          final UserRepository userRepository, final ScoringStrategyFactory scoringStrategyFactory,
                          final AuditMapper auditMapper, final CategoryCommentService categoryCommentService, final GlobalCommentService globalCommentService) {
    this.auditRepository = auditRepository;
    this.projectRepository = projectRepository;
    this.userRepository = userRepository;
    this.scoringStrategyFactory = scoringStrategyFactory;
    this.auditMapper = auditMapper;
    this.categoryCommentService = categoryCommentService;
    this.globalCommentService = globalCommentService;
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
  @Transactional
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

    final Audit saved = auditRepository.save(audit);
    final AuditResponseModel response = mapToResponseModel(saved);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  /**
   * Create a new audit.
   * @param auditRequest the audit request model
   * @param userEmail    the user email
   * @return the created audit response
   * @throws UserNotFoundException    if user not found
   */

  @Override
  @Transactional
  public ResponseEntity<AuditResponseModel> updateAudit(@Valid final AuditRequestModel auditRequest, final String userEmail)
    throws UserNotFoundException, AuditNotFoundException {

    final User user = userRepository.findByUserEmail(userEmail);
    if (user == null) {
      throw new UserNotFoundException("Utilisateur non trouvé");
    }

    final Long auditId = Objects.requireNonNull(auditRequest.getAuditId(), "auditId est obligatoire");
    final Audit audit = auditRepository.findById(auditId)
      .orElseThrow(() -> new AuditNotFoundException("Audit non trouvé: " + auditId));

    if (auditRequest.getQuestions() != null && !auditRequest.getQuestions().isEmpty()) {
      audit.getQuestions().clear();

      for (QuestionRequestModel qr : auditRequest.getQuestions()) {
        final Question q = auditMapper.questionRequestModelToQuestion(qr, audit);
        audit.getQuestions().add(q);
      }
    }

    final Audit saved = auditRepository.save(audit);
    final AuditResponseModel response = mapToResponseModel(saved);
    return ResponseEntity.ok(response);

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
   * Calculates the scoring for a given audit based on its method type.
   * This method retrieves the audit by its ID, maps it to a response model,
   * resolves the appropriate scoring strategy using the {@link ScoringStrategyFactory},
   * performs the scoring calculation, and persists the resulting {@link AuditScore}.
   *
   * @param auditId The ID of the audit to score.
   * @return The {@link AuditScoreDTO} containing the calculated score details.
   * @throws AuditNotFoundException if the audit with the given ID is not found.
   */
  @Override
  public AuditScoreDTO calculateScoringOfAudit(final Long auditId) throws AuditNotFoundException {
    Audit audit = auditRepository.findById(auditId)
      .orElseThrow(() -> new AuditNotFoundException("Audit Not Found"));

    AuditResponseModel auditResponseModel = this.mapToResponseModel(audit);
    MethodsScoringStrategy methodsScoringStrategy = scoringStrategyFactory.resolveStrategyByMethodType(auditResponseModel.getMethod());
    methodsScoringStrategy.calculateScoring(auditResponseModel);

    Map<Long, CategoryComment> commentById = categoryCommentService.findAll().stream()
      .collect(Collectors.toMap(CategoryComment::getId, Function.identity()));

    AuditScore auditScore = this.auditMapper.responseModeltoAuditScore(auditResponseModel, audit, commentById);
    auditScore.getQuestionResponses().forEach(q -> q.setAuditScore(auditScore));
    auditScore.getAuditCategoryResults().forEach(categoryResult -> categoryResult.setAuditScore(auditScore));
    auditScore.getQuestionResponses().forEach(q -> q.setAuditScore(auditScore));

    GlobalComment globalComment = globalCommentService.findByMethodAndLevel(auditScore.getAudit().getMethod(), LevelScore.fromPercentage(auditScore.getGlobalScorePercentage()));
    auditScore.setGlobalComment(globalComment);

    audit.setAuditScore(auditScore);

    auditRepository.save(audit);
    return auditMapper.auditScoretoAuditScoreDto(auditScore);
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
          .map(auditMapper::mapToQuestionResponseModel)
          .collect(Collectors.toList());
    }

    return AuditResponseModel.builder()
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
  }

}
