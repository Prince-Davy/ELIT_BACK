
package com.elit.outildedecision.shared.methodology.repository;

import com.elit.outildedecision.shared.methodology.entity.Methodology;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Methodology repository interface. */
@Repository
public interface MethodologyRepository extends JpaRepository<Methodology, Long> {
  /**
   * Find a Methodology Object by methodology name.
   *
   * @param methodologyName the methodology name
   * @return the methodology matching the methodology name
   */
  Methodology findByName(String methodologyName);
}
