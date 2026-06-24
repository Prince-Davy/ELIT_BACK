package com.elit.outildedecision.audit.service.implementation;

import com.elit.outildedecision.audit.calculate.AuditCategoryResultEnricher;
import com.elit.outildedecision.audit.calculate.MethodsScoringStrategy;
import com.elit.outildedecision.audit.calculate.ScoringStrategyFactory;
import com.elit.outildedecision.audit.dto.AuditRequest;
import com.elit.outildedecision.audit.dto.AuditResponse;
import com.elit.outildedecision.audit.dto.AuditScoreResponse;
import com.elit.outildedecision.audit.dto.QuestionRequest;
import com.elit.outildedecision.audit.dto.QuestionResponse;
import com.elit.outildedecision.audit.entity.Audit;
import com.elit.outildedecision.audit.entity.AuditScore;
import com.elit.outildedecision.audit.mapper.AuditMapper;
import com.elit.outildedecision.audit.repository.AuditRepository;
import com.elit.outildedecision.audit.service.AuditService;
import com.elit.outildedecision.shared.kernel.entity.Project;
import com.elit.outildedecision.shared.kernel.entity.User;
import com.elit.outildedecision.audit.exception.AuditNotFoundException;
import com.elit.outildedecision.shared.exception.BadRequestException;
import com.elit.outildedecision.shared.project.exception.ProjectNotFoundException;
import com.elit.outildedecision.shared.user.exception.UserNotFoundException;
import com.elit.outildedecision.shared.project.repository.ProjectRepository;
import com.elit.outildedecision.shared.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service implementation for Audit operations.
 */
@Service
@Slf4j
public class AuditServiceImpl implements AuditService {

  private static final String PROJECT_NOT_FOUND_MESSAGE = "Ce Projet n'existe pas!";

  private final AuditRepository auditRepository;
  private final ProjectRepository projectRepository;
  private final UserRepository userRepository;
  private final ScoringStrategyFactory scoringStrategyFactory;
  private final AuditMapper auditMapper;
  private final AuditCategoryResultEnricher auditCategoryResultEnricher;

  /**
   * Constructor for AuditServiceImpl.
   *
   * @param auditRepository             the repository for managing audit entities
   * @param projectRepository           the repository for managing project entities
   * @param userRepository              the repository for managing user entities
   * @param scoringStrategyFactory      the factory for creating scoring strategies
   * @param auditMapper                 the mapper for converting between audit entities and DTOs
   * @param auditCategoryResultEnricher the component enriching the audit score with comments
   */
  public AuditServiceImpl(final AuditRepository auditRepository,
                          final ProjectRepository projectRepository,
                          final UserRepository userRepository,
                          final ScoringStrategyFactory scoringStrategyFactory,
                          final AuditMapper auditMapper,
                          final AuditCategoryResultEnricher auditCategoryResultEnricher) {
    this.auditRepository = auditRepository;
    this.projectRepository = projectRepository;
    this.userRepository = userRepository;
    this.scoringStrategyFactory = scoringStrategyFactory;
    this.auditMapper = auditMapper;
    this.auditCategoryResultEnricher = auditCategoryResultEnricher;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional
  public AuditResponse createAudit(final AuditRequest auditRequest, final String userEmail)
      throws ProjectNotFoundException, UserNotFoundException {

    final Project project = projectRepository.findById(auditRequest.getProjectId())
        .orElseThrow(() -> new ProjectNotFoundException(PROJECT_NOT_FOUND_MESSAGE));

    final User user = userRepository.findByUserEmail(userEmail);
    if (user == null) {
      throw new UserNotFoundException("Utilisateur non trouvé");
    }

    final Audit audit = Audit.builder()
        .method(auditRequest.getMethod())
        .project(project)
        .user(user)
        .build();

    final Audit saved = auditRepository.save(audit);
    log.info("Audit {} created for project {} by user {}", saved.getAuditId(), project.getProjectId(), user.getUserId());
    return mapToResponse(saved);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional
  public AuditResponse updateAudit(final AuditRequest auditRequest, final String userEmail)
      throws UserNotFoundException, AuditNotFoundException {

    final User user = userRepository.findByUserEmail(userEmail);
    if (user == null) {
      throw new UserNotFoundException("Utilisateur non trouvé");
    }

    final Long auditId = auditRequest.getAuditId();
    if (auditId == null) {
      throw new BadRequestException("L'identifiant de l'audit est obligatoire");
    }
    final Audit audit = auditRepository.findById(auditId)
        .orElseThrow(() -> new AuditNotFoundException("Audit non trouvé: " + auditId));

    if (auditRequest.getQuestions() != null && !auditRequest.getQuestions().isEmpty()) {
      audit.getQuestions().clear();
      for (QuestionRequest questionRequest : auditRequest.getQuestions()) {
        audit.getQuestions().add(auditMapper.toQuestion(questionRequest, audit));
      }
    }

    final Audit saved = auditRepository.save(audit);
    log.info("Audit {} updated with {} questions by user {}", saved.getAuditId(), saved.getQuestions().size(), user.getUserId());
    return mapToResponse(saved);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional(readOnly = true)
  public List<AuditResponse> getAuditsByProject(final Long projectId) throws ProjectNotFoundException {
    if (!projectRepository.existsById(projectId)) {
      throw new ProjectNotFoundException(PROJECT_NOT_FOUND_MESSAGE);
    }

    return auditRepository.findByProjectProjectIdOrderByCreatedAtDesc(projectId).stream()
        .map(this::mapToResponse)
        .toList();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional
  public AuditScoreResponse calculateScoringOfAudit(final Long auditId) throws AuditNotFoundException {
    final Audit audit = auditRepository.findById(auditId)
        .orElseThrow(() -> new AuditNotFoundException("Audit Not Found"));

    if (audit.getMethod() == null) {
      throw new BadRequestException("La méthode de l'audit est obligatoire pour calculer le score");
    }

    final MethodsScoringStrategy scoringStrategy;
    try {
      scoringStrategy = scoringStrategyFactory.resolveStrategyByMethodType(audit.getMethod());
    } catch (IllegalArgumentException e) {
      throw new BadRequestException("La méthode " + audit.getMethod()
          + " ne supporte pas le calcul de score d'audit", e);
    }
    final AuditScore auditScore = scoringStrategy.calculateScoring(audit);

    auditCategoryResultEnricher.enrichAuditComment(auditScore, audit.getMethod(), auditScore.getAuditCategoryResults());
    audit.setAuditScore(auditScore);

    final Audit persisted = auditRepository.save(audit);
    log.info("Audit score computed for audit {} (method {}, global score {})",
        auditId, audit.getMethod(), persisted.getAuditScore().getGlobalScorePercentage());
    return auditMapper.toAuditScoreResponse(persisted.getAuditScore());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional(readOnly = true)
  public boolean projectExists(final Long projectId) {
    return projectRepository.existsById(projectId);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional(readOnly = true)
  public AuditResponse getAuditByProjectAndAuditId(final Long projectId, final Long auditId)
      throws ProjectNotFoundException, AuditNotFoundException {
    if (!projectRepository.existsById(projectId)) {
      throw new ProjectNotFoundException("This project does not exist: " + projectId);
    }
    final Audit audit = auditRepository.findById(auditId)
        .orElseThrow(() -> new AuditNotFoundException("Audit non trouvé: " + auditId));
    if (!audit.getProject().getProjectId().equals(projectId)) {
      throw new AuditNotFoundException("Cet audit n'appartient pas au projet: " + projectId);
    }
    return mapToResponse(audit);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional(readOnly = true)
  public boolean hasAuditForProject(final Long projectId) throws ProjectNotFoundException {
    if (!projectRepository.existsById(projectId)) {
      throw new ProjectNotFoundException("This project does not exist: " + projectId);
    }
    return auditRepository.countAuditsByProjectId(projectId) > 0;
  }

  /**
   * Maps an audit entity to its response, delegating the base fields to {@link AuditMapper} and
   * computing the user role, the per-question metrics, the aggregated totals and the audit count
   * inside the transactional boundary (the LAZY relations are traversed here).
   *
   * @param audit the audit entity
   * @return the fully populated audit response
   */
  private AuditResponse mapToResponse(final Audit audit) {
    final AuditResponse response = auditMapper.toAuditResponse(audit);
    response.setUserRole(resolveUserRole(audit.getUser()));
    response.getQuestions().forEach(AuditServiceImpl::calculateQuestionMetrics);
    response.setTotalQuestions(response.getQuestions().size());
    response.setTotalWeight(response.getQuestions().stream()
        .map(question -> question.getWeight() != null ? question.getWeight() : 0)
        .reduce(0, Integer::sum));
    response.setTotalAuditCount(auditRepository.countAuditsByProjectId(audit.getProject().getProjectId()));
    return response;
  }

  /**
   * Resolves the user role from its first project permission, defaulting to {@code "Unknown"}.
   *
   * @param user the user owning the audit
   * @return the resolved role label
   */
  private static String resolveUserRole(final User user) {
    if (user.getUserProjectPermissions() != null && !user.getUserProjectPermissions().isEmpty()) {
      return user.getUserProjectPermissions().get(0).getPermission();
    }
    return "Unknown";
  }

  /**
   * Computes the scoring metrics of a question response (weighted score, maximum possible score,
   * percentage and answered flag) from its weight and answer value.
   *
   * @param question the question response to enrich in place
   */
  private static void calculateQuestionMetrics(final QuestionResponse question) {
    final Integer weight = question.getWeight();
    final Integer answerValue = question.getAuditAnswerValue();
    Integer maxPossibleScore = null;
    Integer weightedScore = null;
    Double scorePercentage = null;
    boolean isAnswered = false;

    if (weight != null) {
      maxPossibleScore = 2 * weight;
    }
    if (answerValue != null && weight != null) {
      weightedScore = answerValue * weight;
      isAnswered = true;
      if (maxPossibleScore > 0) {
        scorePercentage = weightedScore.doubleValue() / maxPossibleScore * 100.0;
      }
    }

    question.setMaxPossibleScore(maxPossibleScore);
    question.setWeightedScore(weightedScore);
    question.setScorePercentage(scorePercentage);
    question.setIsAnswered(isAnswered);
  }
}
