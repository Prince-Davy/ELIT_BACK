package com.elit.outildedecision.shared.security;

import com.elit.outildedecision.shared.kernel.entity.Project;

/**
 * Service that handles removal of project-related permissions.
 */
public interface PermissionService {
  /**
   * Remove all permissions related to the given project. Implementations must
   * ensure user associations are detached prior to deletion.
   *
   * @param project the project whose permissions should be removed
   */
  void removePermissions(Project project);
}
