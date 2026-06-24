package com.elit.outildedecision.shared.attachment.repository;

import com.elit.outildedecision.shared.attachment.entity.AttachmentContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for the binary content of attachments ({@code attachment_content} table),
 * keyed by attachment id.
 */
@Repository
public interface AttachmentContentRepository extends JpaRepository<AttachmentContent, Long> {
}
