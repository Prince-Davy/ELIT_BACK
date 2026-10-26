package com.elit.outildedecision.shared.report.mapper;

import com.elit.outildedecision.shared.report.dto.ReportResponse;
import com.elit.outildedecision.shared.report.entity.Report;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper between {@link Report} entities and their response DTOs.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ReportMapper {

  /**
   * Maps a {@link Report} entity to its response DTO. The {@code title} is derived from the file
   * path relative to the project folder and is therefore computed in the service.
   *
   * @param report the report entity
   * @return the corresponding response DTO (without {@code title})
   */
  @Mapping(target = "id", source = "reportId")
  @Mapping(target = "creationDate", source = "createdAt")
  @Mapping(target = "title", ignore = true)
  ReportResponse toResponse(Report report);
}
