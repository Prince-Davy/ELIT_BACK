package com.elit.outildedecision.shared.attachment.repository;

import com.elit.outildedecision.shared.attachment.entity.Attachment;
import com.elit.outildedecision.shared.kernel.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing attachments.
 */
@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
  /**
   * Find all attachments by project.
   * @param project the project
   * @return the list of attachments
   */
  List<Attachment> findAllByProject(Project project);
}
