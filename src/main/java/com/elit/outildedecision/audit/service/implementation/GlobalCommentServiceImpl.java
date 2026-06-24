package com.elit.outildedecision.audit.service.implementation;

import com.elit.outildedecision.audit.entity.comment.GlobalComment;
import com.elit.outildedecision.audit.entity.LevelScore;
import com.elit.outildedecision.audit.repository.GlobalCommentRepository;
import com.elit.outildedecision.audit.service.GlobalCommentService;
import com.elit.outildedecision.shared.util.MethodType;
import org.springframework.stereotype.Service;

/** Global comment service impl class. */
@Service
public class GlobalCommentServiceImpl implements GlobalCommentService {

  private final GlobalCommentRepository globalCommentRepository;

  /**
   * Contructor of Global Comment Service .
   * @param globalCommentRepository the repository accessing to global comment
   *
   */
  public GlobalCommentServiceImpl(final GlobalCommentRepository globalCommentRepository) {
    this.globalCommentRepository = globalCommentRepository;
  }

  /**
   * Finds the global comment matching a method type and evaluation level.
   *
   * <p>The global comment is reference data used only to decorate the computed score; a missing
   * entry must not break the calculation. When no comment is seeded for the (method, level) pair,
   * this returns {@code null} — mirroring the tolerant lookup of category comments in
   * {@link com.elit.outildedecision.audit.calculate.AuditCategoryResultEnricher}.
   *
   * @param methodType the method type
   * @param level the evaluation level (BON, MOYEN, MAUVAIS)
   * @return the matching GlobalComment, or {@code null} if none is seeded
   */
  @Override
  public GlobalComment findByMethodAndLevel(final MethodType methodType, final LevelScore level) {
    return globalCommentRepository.findByMethodAndLevel(methodType, level).orElse(null);
  }
}
