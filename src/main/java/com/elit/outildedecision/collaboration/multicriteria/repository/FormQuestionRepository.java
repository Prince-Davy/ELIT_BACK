package com.elit.outildedecision.collaboration.multicriteria.repository;

import com.elit.outildedecision.collaboration.multicriteria.entity.FormQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Form question repository interface. */
@Repository
public interface FormQuestionRepository extends JpaRepository<FormQuestion, Long> {
}
