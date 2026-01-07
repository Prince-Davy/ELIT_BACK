package com.elit.outildedecision.audit.service.impl;

import com.elit.outildedecision.audit.calculate.AuditCategoryResultEnricher;
import com.elit.outildedecision.audit.calculate.MethodsScoringStrategy;
import com.elit.outildedecision.audit.calculate.ScoringStrategyFactory;

import com.elit.outildedecision.audit.entity.Audit;
import com.elit.outildedecision.audit.entity.AuditScore;
import com.elit.outildedecision.audit.entity.Question;
import com.elit.outildedecision.audit.mapper.AuditMapper;
import com.elit.outildedecision.audit.model.AuditRequestModel;
import com.elit.outildedecision.audit.model.AuditResponseModel;
import com.elit.outildedecision.audit.model.AuditScoreDTO;
import com.elit.outildedecision.audit.model.QuestionResponseModel;
import com.elit.outildedecision.audit.model.QuestionRequestModel;
import com.elit.outildedecision.audit.repository.AuditRepository;
import com.elit.outildedecision.audit.service.AuditService;
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

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
  private final AuditCategoryResultEnricher auditCategoryResultEnricher;


  /**
   * Constructor for AuditServiceImpl.
   * Follows the Dependency Inversion Principle by depending on abstractions.
   *
   * @param auditRepository the repository for managing audit entities
   * @param projectRepository the repository for managing project entities
   * @param userRepository the repository for managing user entities
   * @param scoringStrategyFactory the factory for creating scoring strategies
   * @param auditMapper the mapper for converting between audit models and entities
   * @param auditCategoryResultEnricher the service used for retrieve the comment of audit score
   */
  public AuditServiceImpl(final AuditRepository auditRepository,
                          final ProjectRepository projectRepository,
                          final UserRepository userRepository, final ScoringStrategyFactory scoringStrategyFactory,
                          final AuditMapper auditMapper, final AuditCategoryResultEnricher auditCategoryResultEnricher) {
    this.auditRepository = auditRepository;
    this.projectRepository = projectRepository;
    this.userRepository = userRepository;
    this.scoringStrategyFactory = scoringStrategyFactory;
    this.auditMapper = auditMapper;
    this.auditCategoryResultEnricher = auditCategoryResultEnricher;
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

    MethodsScoringStrategy methodsScoringStrategy = scoringStrategyFactory.resolveStrategyByMethodType(audit.getMethod());
    AuditScore auditScore = methodsScoringStrategy.calculateScoring(audit);

    auditCategoryResultEnricher.enrichAuditComment(auditScore, audit.getMethod(), auditScore.getAuditCategoryResults());

    audit.setAuditScore(auditScore);

    final Audit auditPersisted = auditRepository.save(audit);
    return auditMapper.auditScoretoAuditScoreDto(auditPersisted.getAuditScore());
  }

  /**
   * Map Audit entity to AuditResponseModel.
   * Following Single Responsibility Principle - handles only audit mapping.
   *
   * @param audit the audit entity
   * @return the audit response model
   */
  private AuditResponseModel mapToResponseModel(final Audit audit) {
    // rôle utilisateur (inchangé)
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

    // Calculs agrégés
    Integer totalQuestions = questionResponses.size();
    Integer totalWeight = questionResponses.stream()
      .map(q -> q.getWeight() != null ? q.getWeight() : 0)
      .reduce(0, Integer::sum);

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
      .totalQuestions(totalQuestions)
      .totalWeight(totalWeight)
      .build();
  }

  /**
   * Vérifie si un projet existe à partir de son identifiant.
   *
   * <p>Cette méthode interroge le {@code projectRepository} pour déterminer si un projet
   * avec l'identifiant fourni est présent en base.</p>
   *
   * @param projectId l'identifiant unique du projet (non null)
   * @return {@code true} si le projet existe ; {@code false} sinon
   */
  @Override
  public boolean projectExists(final Long projectId) {
    return projectRepository.existsById(projectId);
  }

  /**
   * Récupère un audit par son identifiant et vérifie qu’il appartient au projet
   *
   * @param projectId l’identifiant unique du projet auquel l’audit est censé appartenir (non null)
   * @param auditId   l’identifiant unique de l’audit à récupérer (non null)
   * @return le modèle de réponse représentant l’audit demandé
   * @throws ProjectNotFoundException si le projet n’existe pas
   * @throws AuditNotFoundException   si l’audit n’existe pas ou n’appartient pas au projet fourni
   */
  @Override
  public AuditResponseModel getAuditByProjectAndAuditId(final Long projectId, final Long auditId)
      throws ProjectNotFoundException, AuditNotFoundException {
    // Vérifier que le projet existe
    if (!projectRepository.existsById(projectId)) {
      throw new ProjectNotFoundException("This project does not exist: " + projectId);
    }
    // Récupérer l'audit et vérifier qu'il appartient bien au projet
    Audit audit = auditRepository.findById(auditId)
      .orElseThrow(() -> new AuditNotFoundException("Audit non trouvé: " + auditId));
    if (!audit.getProject().getProjectId().equals(projectId)) {
      throw new AuditNotFoundException("Cet audit n'appartient pas au projet: " + projectId);
    }
    return mapToResponseModel(audit);
  }


  /**
   * Indique si au moins un audit existe pour un projet donné.
   * Elle retourne {@code true} si le nombre est strictement supérieur à 0.
   *
   * @param projectId l’identifiant unique du projet (non null)
   * @return {@code true} si au moins un audit est associé au projet ; {@code false} sinon
   * @throws ProjectNotFoundException si le projet n’existe pas
   */
  @Override
  public boolean hasAuditForProject(final Long projectId) throws ProjectNotFoundException {
    if (!projectRepository.existsById(projectId)) {
      throw new ProjectNotFoundException("This project does not exist: " + projectId);
    }
    return auditRepository.countAuditsByProjectId(projectId) > 0;
  }
}
