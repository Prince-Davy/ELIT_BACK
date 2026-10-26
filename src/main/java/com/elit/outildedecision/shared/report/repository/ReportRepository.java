
package com.elit.outildedecision.shared.report.repository;

import com.elit.outildedecision.shared.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.elit.outildedecision.shared.util.ReportTypes;


import java.util.List;
/**
 * This interface represents a Spring repository for accessing and managing Report entities.
 * It extends the JpaRepository interface and is used for CRUD operations on Report objects.
 */
@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

  /**
   * Returns a list of reports associated with the specified project ID.
   *
   * @param projectId the ID of the project to retrieve reports for
   * @return a list of reports associated with the specified project ID
   */
  @Query("select r from Report r where r.project.projectId = ?1")
  List<Report> findReportsByProjectId(Long projectId);

  /**
   * Looks up a report in the requested project only. Analysis IDs can overlap across
   * different report types and projects, so all four criteria are required.
   *
   * @param projectId the project identifier
   * @param analyseId the analysis identifier
   * @param type the report type
   * @param iteration the report iteration
   * @return the matching report or null if it does not exist
   */
  @Query("select r from Report r where r.project.projectId = :projectId "
    + "and r.analyseId = :analyseId and r.type = :type and r.iteration = :iteration")
  Report findReportByProjectAndAnalyseAndTypeAndIteration(
    @Param("projectId") Long projectId,
    @Param("analyseId") Long analyseId,
    @Param("type") ReportTypes type,
    @Param("iteration") Integer iteration);


}
