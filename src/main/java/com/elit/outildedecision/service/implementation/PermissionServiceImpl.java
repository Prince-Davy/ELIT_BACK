package com.elit.outildedecision.service.implementation;

import com.elit.outildedecision.entity.Project;
import com.elit.outildedecision.entity.ProjectUserPermission;
import com.elit.outildedecision.repository.ProjectUserPermissionRepository;
import com.elit.outildedecision.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PermissionServiceImpl implements PermissionService {
  @Autowired
  private ProjectUserPermissionRepository projectUserPermissionRepository;

  @Override
  public void removePermissions(Project project) {
    List<ProjectUserPermission> permissions = project.getProjectUserPermissions();
    for (ProjectUserPermission pup : permissions) {
      pup.getUser().detachPermission(pup);
      projectUserPermissionRepository.delete(pup);
    }
  }
}
