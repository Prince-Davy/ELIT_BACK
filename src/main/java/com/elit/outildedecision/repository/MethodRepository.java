package com.elit.outildedecision.repository;

import com.elit.outildedecision.entity.Method;
import com.elit.outildedecision.entity.Methodology;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MethodRepository extends JpaRepository<Method, Long> {

    @Query("SELECT m FROM Method m WHERE m.methodology.methodologyId = ?1")
    List<Method> findAllMethodsByMethodologyId(Long methodologyId);

    @Query("SELECT m FROM Method m")
    List<Method> findAllMethods();
}
