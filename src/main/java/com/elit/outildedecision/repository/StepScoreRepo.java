
package com.elit.outildedecision.repository;

import com.elit.outildedecision.entity.StepScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StepScoreRepo extends JpaRepository<StepScore, Long> {
}
