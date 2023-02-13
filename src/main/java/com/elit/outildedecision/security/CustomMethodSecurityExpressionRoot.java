package com.elit.outildedecision.security;

import com.elit.outildedecision.entity.User;
import com.elit.outildedecision.service.UserService;
import com.elit.outildedecision.util.ProjectPermession;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.List;

public class CustomMethodSecurityExpressionRoot
        extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {
    private Object filterObject;
    private Object returnObject;
    private Object target;
    private UserService userService;

    public CustomMethodSecurityExpressionRoot(Authentication authentication) {
        super(authentication);
    }
    public boolean isProjectOwner(Long projectId){
        List<String> permissions = new ArrayList<>();
        try {
            User user = userService.findByEmail((String) this.getPrincipal());

            user.getUserProjectPermissions().stream()
                    .filter(p -> p.getProject().getProjectId().equals(projectId))
                    .forEach(p -> permissions.add(p.getPermission()));

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return permissions.contains(ProjectPermession.CDP.name());
    }
    public boolean isContributer(Long projectId){
        List<String> permissions = new ArrayList<>();
        try {
            User user = userService.findByEmail((String) this.getPrincipal());
            user.getUserProjectPermissions().stream()
                    .filter(p -> p.getProject().getProjectId().equals(projectId))
                    .forEach(p -> permissions.add(p.getPermission()));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        System.out.println(permissions.contains(ProjectPermession.CDP.name())
                || permissions.contains(ProjectPermession.CONTRIBUTER.name())
                || permissions.contains(ProjectPermession.OBSERVER.name()));
        return permissions.contains(ProjectPermession.CDP.name())
                || permissions.contains(ProjectPermession.CONTRIBUTER.name())
                || permissions.contains(ProjectPermession.OBSERVER.name());
    }


    @Override
    public void setFilterObject(Object filterObject) {
        this.filterObject = filterObject;
    }

    @Override
    public Object getFilterObject() {
        return filterObject;
    }

    @Override
    public void setReturnObject(Object returnObject) {
        this.returnObject = returnObject;
    }

    @Override
    public Object getReturnObject() {
        return returnObject;
    }

    @Override
    public Object getThis() {
        return target;
    }
    public void setThis(Object target) {
        this.target = target;
    }
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
