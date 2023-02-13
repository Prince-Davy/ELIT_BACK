package com.elit.outildedecision.repository;

import com.elit.outildedecision.entity.Methodology;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MethodologyRepository extends JpaRepository<Methodology, Long> {

    @Query("SELECT m FROM Methodology m WHERE m.name = ?1")
    Methodology findMethodologyByName(String methodologyName);

}
