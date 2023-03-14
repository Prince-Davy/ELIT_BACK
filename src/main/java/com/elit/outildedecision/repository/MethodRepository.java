
package com.elit.outildedecision.repository;

import com.elit.outildedecision.entity.Method;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MethodRepository extends JpaRepository<Method, Long> {
  /**
   * Find a list of Method Objects by methodology id.
   *
   * @param methodologyId the methodology id
   * @return the method matching the methodology id
   */
  @Query("SELECT m FROM Method m "
    + "WHERE m.methodology.methodologyId = ?1")
  List<Method> findAllMethodsByMethodologyId(Long methodologyId);
}
