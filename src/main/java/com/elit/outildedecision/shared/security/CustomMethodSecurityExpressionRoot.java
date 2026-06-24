
package com.elit.outildedecision.shared.security;

import com.elit.outildedecision.shared.user.service.UserService;
import com.elit.outildedecision.shared.util.ProjectPermession;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

/**
 * Root d'expressions personnalisées utilisée par Spring Security pour évaluer
 * {@code @PreAuthorize}/{@code @PostAuthorize} dans les services/contrôleurs.
 * <p>
 * Cette implémentation expose des méthodes SpEL comme
 * {@code isProjectOwner(#projectId)}, {@code isContributer(#projectId)} ou
 * {@code isObserver(#projectId)}.
 * </p>
 *
 * <h2>Extension</h2>
 * Cette classe n'est pas conçue pour être étendue; elle est déclarée {@code final}
 * afin de satisfaire la règle Checkstyle {@code DesignForExtension}.
 */

public class CustomMethodSecurityExpressionRoot extends SecurityExpressionRoot
  implements MethodSecurityExpressionOperations {

  private Object filterObject;
  private Object returnObject;
  private Object target;

  private UserService userService;


  /**
   * Construit une root d'expressions custom basée sur l'authentication courante.
   *
   * @param authentication le contexte d'authentification Spring Security
   */
  public CustomMethodSecurityExpressionRoot(final Authentication authentication) {
    super(authentication);
  }


  /**
   * Checks if the authenticated user is the owner of the project with the given ID.
   *
   * @param projectId the ID of the project to check ownership for
   * @return true if the authenticated user is the owner of the project, false otherwise
   */
  public boolean isProjectOwner(final Long projectId) {
    return projectPermissions(projectId).contains(ProjectPermession.CDP.name());
  }

  /**
   * Checks if the authenticated user is the owner of the audit and project with the given auditId.
   *
   * @param auditId the ID of the audit to check ownership for
   * @return true if the authenticated user is the owner of the project and audit, false otherwise
   */
  public boolean isProjectOwnerByAuditId(final Long auditId) {
    try {
      final UserDetails userDetails = (UserDetails) this.getPrincipal();
      return userService.getProjectPermissionsByAudit(userDetails.getUsername(), auditId)
        .contains(ProjectPermession.CDP.name());
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Checks if the authenticated user has contributor permissions for the project with the given ID.
   *
   * @param projectId the ID of the project to check permissions for
   * @return true if the authenticated user has contributor permissions for the project, false otherwise
   */
  public boolean isContributer(final Long projectId) {
    final List<String> permissions = projectPermissions(projectId);
    return permissions.contains(ProjectPermession.CDP.name())
      || permissions.contains(ProjectPermession.CONTRIBUTER.toString());
  }

  /**
   * Checks if the authenticated user has Observer permissions for the project with the given ID.
   *
   * @param projectId the ID of the project to check permissions for
   * @return true if the authenticated user has Observer permissions for the project, false otherwise
   */
  public boolean isObserver(final Long projectId) {
    return projectPermissions(projectId).contains(ProjectPermession.OBSERVER.toString());
  }

  /**
   * Loads the permission labels held by the authenticated user on the given project.
   *
   * @param projectId the id of the project to check permissions for
   * @return the permission labels (empty when unauthenticated or on any lookup error)
   */
  private List<String> projectPermissions(final Long projectId) {
    try {
      final UserDetails userDetails = (UserDetails) this.getPrincipal();
      return userService.getProjectPermissions(userDetails.getUsername(), projectId);
    } catch (Exception e) {
      return List.of();
    }
  }

  /**
   * Returns the object that was the filter object being processed when the method was invoked.
   *
   * @return the filter object
   */
  @Override
  public Object getFilterObject() {
    return filterObject;
  }

  /**
   * Sets the object that was the filter object being processed when the method was invoked.
   *
   * @param filterObject the filter object
   */
  @Override
  public void setFilterObject(final Object filterObject) {
    this.filterObject = filterObject;
  }

  /**
   * Returns the object that the secured method returned (or null if void).
   *
   * @return the return object
   */
  @Override
  public Object getReturnObject() {
    return returnObject;
  }

  /**
   * Sets the object that the secured method returned (or null if void).
   *
   * @param returnObject the return object
   */
  @Override
  public void setReturnObject(final Object returnObject) {
    this.returnObject = returnObject;
  }

  /**
   * Returns the object that represents the class or instance being invoked.
   *
   * @return the target object
   */
  @Override
  public Object getThis() {
    return target;
  }

  /**
   * Sets the target object for the security expression evaluation.
   *
   * @param target the target object to use
   */
  public void setThis(final Object target) {
    this.target = target;
  }

  /**
   * Sets the user service to use for retrieving user data.
   *
   * @param userService the user service to use
   */
  public void setUserService(final UserService userService) {
    this.userService = userService;
  }
}

