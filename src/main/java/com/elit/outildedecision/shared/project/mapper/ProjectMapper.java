package com.elit.outildedecision.shared.project.mapper;

import com.elit.outildedecision.shared.kernel.entity.Project;
import com.elit.outildedecision.shared.project.entity.ProjectUserPermission;
import com.elit.outildedecision.shared.project.dto.ContributorRequest;
import com.elit.outildedecision.shared.project.dto.ProjectResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for the project context. Maps the direct, side-effect-free fields of a
 * {@link Project} and the contributor projections; the computed fields (analysis status,
 * multicriteria/predictibility lists, chef names) stay in the service.
 */
@Mapper(componentModel = "spring")
public interface ProjectMapper {

  /**
   * Maps the base fields of a project to its response representation. The collections and the
   * computed fields (chef names, analysis status, multicriteria list, A.I. result) are left to
   * the service, as is the form/project state which the creation and update flows do not expose.
   *
   * @param project the project entity to map
   * @return the corresponding {@link ProjectResponse} with only the base fields populated
   */
  @Mapping(target = "chefLasName", ignore = true)
  @Mapping(target = "chefFirstName", ignore = true)
  @Mapping(target = "formProgressStatus", ignore = true)
  @Mapping(target = "projectState", ignore = true)
  @Mapping(target = "contributors", ignore = true)
  @Mapping(target = "attachments", ignore = true)
  @Mapping(target = "multiCriteriaAnalysisList", ignore = true)
  @Mapping(target = "multiCriteriaStatus", ignore = true)
  @Mapping(target = "resultAi", ignore = true)
  @Mapping(target = "iaStatus", ignore = true)
  @Mapping(target = "collaborativeStatus", ignore = true)
  @Mapping(target = "auditStatus", ignore = true)
  ProjectResponse toResponseBase(Project project);

  /**
   * Maps a project user permission to a contributor including the account information
   * (email and admin flag).
   *
   * @param permission the project user permission to map
   * @return the corresponding {@link ContributorRequest}
   */
  @Mapping(target = "contributerId", source = "user.userId")
  @Mapping(target = "firstName", source = "user.userFirstName")
  @Mapping(target = "lastName", source = "user.userLastName")
  @Mapping(target = "email", source = "user.userEmail")
  @Mapping(target = "isAdmin", source = "user.isAdmin")
  @Mapping(target = "func", source = "userFunction")
  @Mapping(target = "avatarPath", source = "user.userAvatarPath")
  @Mapping(target = "role", source = "permission")
  ContributorRequest toContributor(ProjectUserPermission permission);

  /**
   * Maps a project user permission to a contributor without the account information
   * (email and admin flag are left null), as exposed by the project list journey.
   *
   * @param permission the project user permission to map
   * @return the corresponding {@link ContributorRequest}
   */
  @Mapping(target = "contributerId", source = "user.userId")
  @Mapping(target = "firstName", source = "user.userFirstName")
  @Mapping(target = "lastName", source = "user.userLastName")
  @Mapping(target = "email", ignore = true)
  @Mapping(target = "isAdmin", ignore = true)
  @Mapping(target = "func", source = "userFunction")
  @Mapping(target = "avatarPath", source = "user.userAvatarPath")
  @Mapping(target = "role", source = "permission")
  ContributorRequest toContributorWithoutAccount(ProjectUserPermission permission);
}
