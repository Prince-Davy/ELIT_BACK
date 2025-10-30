package com.elit.outildedecision.service.implementation;

import com.elit.outildedecision.entity.Project;
import com.elit.outildedecision.entity.ProjectUserPermission;
import com.elit.outildedecision.entity.User;
import com.elit.outildedecision.repository.ProjectUserPermissionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Arrays;
import java.util.List;
import static org.mockito.Mockito.*;

class PermissionServiceImplTest {
    @Mock
    private ProjectUserPermissionRepository projectUserPermissionRepository;
    @InjectMocks
    private PermissionServiceImpl permissionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void removePermissions_shouldDetachAndDeleteAllPermissions() {
        // Arrange
        User user1 = mock(User.class);
        User user2 = mock(User.class);
        ProjectUserPermission pup1 = mock(ProjectUserPermission.class);
        ProjectUserPermission pup2 = mock(ProjectUserPermission.class);
        when(pup1.getUser()).thenReturn(user1);
        when(pup2.getUser()).thenReturn(user2);
        List<ProjectUserPermission> permissions = Arrays.asList(pup1, pup2);
        Project project = mock(Project.class);
        when(project.getProjectUserPermissions()).thenReturn(permissions);

        // Act
        permissionService.removePermissions(project);

        // Assert
        verify(user1).detachPermission(pup1);
        verify(user2).detachPermission(pup2);
        verify(projectUserPermissionRepository).delete(pup1);
        verify(projectUserPermissionRepository).delete(pup2);
    }

    @Test
    void removePermissions_shouldHandleEmptyPermissionsList() {
        // Arrange
        Project project = mock(Project.class);
        when(project.getProjectUserPermissions()).thenReturn(List.of());

        // Act
        permissionService.removePermissions(project);

        // Assert
        verify(project, times(1)).getProjectUserPermissions();
        verifyNoInteractions(projectUserPermissionRepository);
    }
}

