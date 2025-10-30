package com.elit.outildedecision.repository;

import com.elit.outildedecision.entity.Attachment;
import com.elit.outildedecision.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
  /**
   * Find all attachments by project.
   * @param project the project
   * @return the list of attachments
   */
  List<Attachment> findAllByProject(Project project);
}
