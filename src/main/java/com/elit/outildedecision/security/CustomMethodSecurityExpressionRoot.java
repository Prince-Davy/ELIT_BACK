
package com.elit.outildedecision.security;

import com.elit.outildedecision.entity.User;
import com.elit.outildedecision.service.UserService;
import com.elit.outildedecision.util.ProjectPermession;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.List;

/**
 * CustomMethodSecurityExpressionRoot is an implementation of Spring Security's MethodSecurityExpressionOperations
 * interface that provides custom security expressions for checking project ownership and contributor permissions.
 */
public class CustomMethodSecurityExpressionRoot extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {

  /**
   * The object that was the filter object being processed when the method was invoked.
   */
  private Object filterObject;

  /**
   * The object that the secured method returned (or null if void).
   */
  private Object returnObject;

  /**
   * The object that represents the class or instance being invoked.
   */
  private Object target;

  /**
   * The service used to retrieve information about the user.
   */
  private UserService userService;
  /**
   * Constructs a new CustomMethodSecurityExpressionRoot instance with the given authentication object.
   *
   * @param authentication the authentication object to use
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
    List<String> permissions = new ArrayList<>();
    try {
      User user = userService.findByEmail((String) this.getPrincipal());

      user.getUserProjectPermissions().stream()
        .filter(p -> p.getProject().getProjectId().equals(projectId))
        .forEach(p -> permissions.add(p.getPermission()));

    } catch (Exception e) {
      return false;
    }
    return permissions.contains(ProjectPermession.CDP.name());
  }

  /**
   * Checks if the authenticated user has contributor permissions for the project with the given ID.
   *
   * @param projectId the ID of the project to check permissions for
   * @return true if the authenticated user has contributor permissions for the project, false otherwise
   */
  public boolean isContributer(final Long projectId) {
    List<String> permissions = new ArrayList<>();
    try {
      User user = userService.findByEmail((String) this.getPrincipal());
      user.getUserProjectPermissions().stream()
        .filter(p -> p.getProject().getProjectId().equals(projectId))
        .forEach(p -> permissions.add(p.getPermission()));
    } catch (Exception e) {
      return false;
    }
    return permissions.contains(ProjectPermession.CDP.name())
      || permissions.contains(ProjectPermession.CONTRIBUTER.name())
      || permissions.contains(ProjectPermession.OBSERVER.name());
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

