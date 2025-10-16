package com.elit.outildedecision.audit.service;

import com.elit.outildedecision.audit.entity.comment.GlobalComment;
import com.elit.outildedecision.audit.entity.LevelScore;
import com.elit.outildedecision.util.MethodType;

public interface GlobalCommentService {

  /**
   * finds a catagory comment by its associated categoty and evaluation level and method type.
   * @param methodType the method type
   * @param level he evaluation level (BON, MOYEN, MAUVAIS)
   * @return a GlobalComment
   */
  GlobalComment findByMethodAndLevel(MethodType methodType, LevelScore level);
}
