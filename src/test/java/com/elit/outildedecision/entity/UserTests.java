package com.elit.outildedecision.entity;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTests {
    @Test
    void testToString(){
        User user = new User(null, 1L, "username", "userLastName", "userFirstName", "userEmail", "/", null, null, null, null, null, null);
        final String useString="User{"
        + "userId="
        + "1"
        + ", username='"
        + "username"
        + '\''
        + ", userLastName='"
        + "userLastName"
        + '\''
        + ", userFirstName='"
        + "userFirstName"
        + '\''
        + ", userEmail='"
        + "userEmail"
        + '\''
        + ", userAvatar='"
        + "/"
        + '\''
        + '}';
        assertEquals(useString, user.toString());
    }

    @Test
    void testdetachPermission(){
        User user = new User(null, 1L, "username", "userLastName", "userFirstName", "userEmail", "/", null, null, null, null, null, null);
        Project p = new Project();
        ProjectUserPermission pup = new ProjectUserPermission();
        pup.setId(new ProjectPropertyKey());
        pup.setUser(user);
        pup.setProject(p);
        pup.setPermission("permission");
        ArrayList<ProjectUserPermission> list = new ArrayList<ProjectUserPermission>();
        list.add(pup);
        user.setUserProjectPermissions(list);
        user.detachPermission(pup);
        assertNotSame(pup.getUser(),user);
    }

}
