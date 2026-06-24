package com.elit.outildedecision.audit.service;

import com.elit.outildedecision.audit.entity.comment.GlobalComment;
import com.elit.outildedecision.audit.entity.LevelScore;
import com.elit.outildedecision.shared.util.MethodType;

/** Global comment service interface. */
public interface GlobalCommentService {

  /**
   * Finds the global comment matching a method type and evaluation level.
   *
   * @param methodType the method type
   * @param level the evaluation level (BON, MOYEN, MAUVAIS)
   * @return the matching GlobalComment, or {@code null} if none is seeded for the pair
   */
  GlobalComment findByMethodAndLevel(MethodType methodType, LevelScore level);
}
