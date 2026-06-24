package com.elit.outildedecision.collaboration.multicriteria.repository;

import com.elit.outildedecision.collaboration.multicriteria.entity.StepScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Step score repository interface. */
@Repository
public interface StepScoreRepository extends JpaRepository<StepScore, Long> {
}
