package com.elit.outildedecision.audit.repository;

import com.elit.outildedecision.audit.entity.comment.GlobalComment;
import com.elit.outildedecision.audit.entity.LevelScore;
import com.elit.outildedecision.shared.util.MethodType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/** Global comment repository interface. */
@Repository
public interface GlobalCommentRepository extends JpaRepository<GlobalComment, Long> {

  /**
   * finds a catagory comment by its associated categoty and evaluation level and method type .
   * @param methodType the method type
   * @param level he evaluation level (BON, MOYEN, MAUVAIS)
   * @return the comment returned from level and method
   */
  Optional<GlobalComment> findByMethodAndLevel(MethodType methodType, LevelScore level);

}
