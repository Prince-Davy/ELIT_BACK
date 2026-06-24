package com.elit.outildedecision.collaboration.report.repository;

import com.elit.outildedecision.collaboration.report.entity.ReportContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for the binary content of reports ({@code report_content} table), keyed by report id.
 */
@Repository
public interface ReportContentRepository extends JpaRepository<ReportContent, Long> {
}
