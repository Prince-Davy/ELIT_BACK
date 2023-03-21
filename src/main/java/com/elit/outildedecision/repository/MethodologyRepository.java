
package com.elit.outildedecision.repository;

import com.elit.outildedecision.entity.Methodology;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
