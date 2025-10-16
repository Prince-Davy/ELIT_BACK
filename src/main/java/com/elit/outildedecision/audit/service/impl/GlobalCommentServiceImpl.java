package com.elit.outildedecision.audit.service.impl;

import com.elit.outildedecision.audit.entity.comment.GlobalComment;
import com.elit.outildedecision.audit.entity.LevelScore;
import com.elit.outildedecision.audit.repository.GlobalCommentRepository;
import com.elit.outildedecision.audit.service.GlobalCommentService;
import com.elit.outildedecision.util.MethodType;
import org.springframework.stereotype.Service;

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
   * finds a catagory comment by its associated categoty and evaluation level and method type.
   * @param methodType the method type
   * @param level he evaluation level (BON, MOYEN, MAUVAIS)
   * @return a GlobalComment
   */
  @Override
  public GlobalComment findByMethodAndLevel(final MethodType methodType, final LevelScore level) {
    return globalCommentRepository.findByMethodAndLevel(methodType, level)
      .orElseThrow(() -> new IllegalArgumentException("Comment not found for level " + level + " and type : " + methodType));
  }
}
