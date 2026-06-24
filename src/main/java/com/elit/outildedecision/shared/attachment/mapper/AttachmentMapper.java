package com.elit.outildedecision.shared.attachment.mapper;

import com.elit.outildedecision.shared.attachment.entity.Attachment;
import com.elit.outildedecision.shared.attachment.dto.AttachmentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * MapStruct mapper converting {@link Attachment} entities to {@link AttachmentResponse} DTOs.
 */
@Mapper(componentModel = "spring")
public interface AttachmentMapper {

  /**
   * Maps an attachment entity to its response representation.
   *
   * <p>The legacy field wiring is preserved for strict API compatibility: the historical
   * controller mapped the creator first name into {@code creatorLastName} and the creator last
   * name into {@code creatorFirstName}. Keeping the same wiring avoids any change in the JSON
   * returned to the front.
   *
   * @param attachment the attachment entity to map
   * @return the corresponding {@link AttachmentResponse}
   */
  @Mapping(target = "creatorLastName", source = "creator.userFirstName")
  @Mapping(target = "creatorFirstName", source = "creator.userLastName")
  AttachmentResponse toResponse(Attachment attachment);

  /**
   * Maps a list of attachment entities to their response representations.
   *
   * @param attachments the attachment entities to map
   * @return the corresponding list of {@link AttachmentResponse}
   */
  List<AttachmentResponse> toResponseList(List<Attachment> attachments);
}
