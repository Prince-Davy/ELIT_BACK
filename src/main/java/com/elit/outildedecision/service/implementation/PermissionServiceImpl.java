package com.elit.outildedecision.service.implementation;

import com.elit.outildedecision.entity.Project;
import com.elit.outildedecision.entity.ProjectUserPermission;
import com.elit.outildedecision.repository.ProjectUserPermissionRepository;
import com.elit.outildedecision.service.PermissionService;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Implementation of {@link PermissionService} responsible for removing project
 * permissions.
 */
@Service
public final class PermissionServiceImpl implements PermissionService {
  private final ProjectUserPermissionRepository projectUserPermissionRepository;

  /**
   * Constructor for PermissionServiceImpl.
   * @param projectUserPermissionRepository the repository for project user permissions
   */
  public PermissionServiceImpl(final ProjectUserPermissionRepository projectUserPermissionRepository) {
    this.projectUserPermissionRepository = projectUserPermissionRepository;
  }

  @Override
  public void removePermissions(final Project project) {
    List<ProjectUserPermission> permissions = project.getProjectUserPermissions();
    for (ProjectUserPermission pup : permissions) {
      pup.getUser().detachPermission(pup);
      projectUserPermissionRepository.delete(pup);
    }
  }
}
