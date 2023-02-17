package com.elit.outildedecision.repository;

import com.elit.outildedecision.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
  @Query("select r from Report r " +
    "where r.project.projectId = ?1")
  List<Report> findReportsByProjectId(Long projectId);

  @Query("select r from Report r " +
    "where r.analyseId = ?1 " +
    "and r.type = ?2")
  Report findReportByAnalyseIdAndType(Long analyseId, String type);

  @Query("select r from Report r " +
    "where r.analyseId = ?1 " +
    "and r.type = ?2 " +
    "and r.iteration = ?3")
  Report findReportByAnalyseIdAndType2(Long analyseId, String type, Integer iteration);
}
