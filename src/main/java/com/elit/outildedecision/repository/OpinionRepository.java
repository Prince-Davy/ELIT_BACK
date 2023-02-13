package com.elit.outildedecision.repository;

import com.elit.outildedecision.entity.Opinion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OpinionRepository extends JpaRepository<Opinion, Long> {
    @Query("select o from Opinion o where o.user.userId = ?1")
    Opinion findByUserId(Long userId);
}
