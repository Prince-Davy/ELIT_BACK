package com.elit.outildedecision.ai.kiviat.service.implementation;

import com.elit.outildedecision.ai.answer.entity.AiAnswer;
import com.elit.outildedecision.ai.answer.exception.AiAnswerNotFoundException;
import com.elit.outildedecision.ai.answer.repository.AiAnswerRepository;
import com.elit.outildedecision.ai.kiviat.calculate.KiviatScoreCalculator;
import com.elit.outildedecision.ai.kiviat.dto.KiviatMatrixQuestion;
import com.elit.outildedecision.ai.kiviat.dto.KiviatResponse;
import com.elit.outildedecision.ai.kiviat.dto.KiviatScores;
import com.elit.outildedecision.ai.kiviat.exception.UnsupportedKiviatMethodologyException;
import com.elit.outildedecision.ai.kiviat.mapper.KiviatMapper;
import com.elit.outildedecision.ai.kiviat.service.KiviatService;
import com.elit.outildedecision.ai.result.entity.ResultAi;
import com.elit.outildedecision.ai.result.exception.ResultAiNotFoundException;
import com.elit.outildedecision.ai.result.repository.ResultIaRepository;
import com.elit.outildedecision.shared.util.JsonResourceLoader;
import com.elit.outildedecision.shared.util.MethodologyType;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Default {@link KiviatService} implementation: loads the Kiviat matrix and computes radar values.
 */
@Slf4j
@Service
public class KiviatServiceImpl implements KiviatService {

  private final AiAnswerRepository aiAnswerRepository;
  private final ResultIaRepository resultIaRepository;
  private final JsonResourceLoader jsonResourceLoader;
  private final KiviatMapper kiviatMapper;
  private final KiviatScoreCalculator kiviatScoreCalculator;

  @Value("${file.kiviatProject-file}")
  private String kiviatProjectFile;

  /** Loaded once at startup; {@code volatile} so reads see a fully published list. */
  // S3077: the referenced value is always an immutable List (List.of / List.copyOf) and is only ever
  // reassigned wholesale, so volatile gives safe publication without needing a thread-safe collection.
  @SuppressWarnings("java:S3077")
  private volatile List<KiviatMatrixQuestion> questions = List.of();

  /**
   * Constructor for KiviatServiceImpl.
   *
   * @param aiAnswerRepository get answers from users
   * @param resultIaRepository get methodology choice from AI
   * @param jsonResourceLoader    loads the matrix file with graceful degradation
   * @param kiviatMapper          maps computed scores to the response DTO
   * @param kiviatScoreCalculator computes the project radar scores from the answers
   */
  public KiviatServiceImpl(final AiAnswerRepository aiAnswerRepository,
                           final ResultIaRepository resultIaRepository,
                           final JsonResourceLoader jsonResourceLoader,
                           final KiviatMapper kiviatMapper,
                           final KiviatScoreCalculator kiviatScoreCalculator) {
    this.aiAnswerRepository = aiAnswerRepository;
    this.resultIaRepository = resultIaRepository;
    this.jsonResourceLoader = jsonResourceLoader;
    this.kiviatMapper = kiviatMapper;
    this.kiviatScoreCalculator = kiviatScoreCalculator;
  }

  /**
   * Loads the Kiviat project matrix from the configured JSON file. On failure the service keeps an
   * empty matrix so that subsequent calls degrade gracefully instead of crashing startup.
   */
  @PostConstruct
  void init() {
    final TypeReference<List<KiviatMatrixQuestion>> type = new TypeReference<>() { };
    questions = List.copyOf(jsonResourceLoader.loadList(kiviatProjectFile, type, null, List.of()));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional(readOnly = true)
  public KiviatResponse getKiviatMethodologyValues(final Long projectId) {
    final ResultAi resultAi = resultIaRepository.findByProject_ProjectId(projectId)
        .orElseThrow(() -> new ResultAiNotFoundException("No result ai found for this projectId"));

    final MethodologyType methodology = MethodologyType.find(resultAi.getPredictedMethod());
    final KiviatScores scores = referenceScores(methodology);

    log.info("Generating Kiviat methodology diagram for projectId={} methodology={}", projectId, methodology);
    return kiviatMapper.toResponse(scores);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional(readOnly = true)
  public KiviatResponse getKiviatProjetValues(final Long projectId) {
    final List<AiAnswer> answers = aiAnswerRepository.findByProject_ProjectId(projectId);
    if (answers == null || answers.isEmpty()) {
      log.error("No AI answers found for projectId={}", projectId);
      throw new AiAnswerNotFoundException("No answer ai found for this projectId");
    }

    final KiviatScores scores = kiviatScoreCalculator.computeProjectScores(questions, answers);

    log.info("Generating Kiviat project diagram for projectId={}", projectId);
    return kiviatMapper.toResponse(scores);
  }

  /**
   * Returns the frozen Kiviat reference axis values for a recommended methodology.
   *
   * @param methodology the AI recommended methodology
   * @return the reference scores
   */
  private KiviatScores referenceScores(final MethodologyType methodology) {
    return switch (methodology) {
      case HYBRID -> new KiviatScores(methodology, 3, 2.5, 2.5, 3, 3);
      case AGILE -> new KiviatScores(methodology, 5, 4.5, 1.5, 1, 5);
      case CLASSIC -> new KiviatScores(methodology, 1, 1.5, 4.5, 5, 1);
      default -> {
        log.error("No Kiviat reference axes for methodology={}", methodology);
        throw new UnsupportedKiviatMethodologyException("Unknown methodology type: " + methodology);
      }
    };
  }
}
