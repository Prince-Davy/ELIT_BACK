package com.elit.outildedecision.shared.security;

import com.elit.outildedecision.shared.kernel.entity.Project;
import com.elit.outildedecision.shared.project.entity.ProjectUserPermission;
import com.elit.outildedecision.shared.project.repository.ProjectUserPermissionRepository;
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

  /** {@inheritDoc} */
  @Override
  public void removePermissions(final Project project) {
    List<ProjectUserPermission> permissions = project.getProjectUserPermissions();
    for (ProjectUserPermission pup : permissions) {
      pup.getUser().detachPermission(pup);
      projectUserPermissionRepository.delete(pup);
    }
  }
}
