package com.elit.outildedecision.shared.security;

import com.elit.outildedecision.shared.kernel.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

 class AppUserPrincipalTest {

  @Test
   void appUserPrincipalTest(){
    AppUserPrincipal appUserPrincipal = new AppUserPrincipal(new User());
    assertNotNull(appUserPrincipal);
  }

  @Test
    void gettersTest(){
    User user = new User();
    user.setUserEmail("user@user.com");
    user.setUserFirstName("firstName");
    user.setUserLastName("lastName");
    user.setUserPassword("password");
    AppUserPrincipal appUserPrincipal = new AppUserPrincipal(user);
    assertEquals("user@user.com",appUserPrincipal.getUsername());
    assertEquals("password",appUserPrincipal.getPassword());
    assertEquals("firstName",appUserPrincipal.getUserFirstName());
    assertEquals("lastName",appUserPrincipal.getUserLastName());
  }

  @Test
   void booleanTests(){
    AppUserPrincipal appUserPrincipal = new AppUserPrincipal(new User());
    assertTrue(appUserPrincipal.isEnabled());
    assertTrue(appUserPrincipal.isAccountNonLocked());
    assertTrue(appUserPrincipal.isAccountNonExpired());
    assertTrue(appUserPrincipal.isCredentialsNonExpired());
  }

  @Test
   void getAuthoritiesTest(){
    User userAdmin = new User();
    userAdmin.setIsAdmin(true);
    User userUser = new User();
    userUser.setIsAdmin(false);
    Collection<GrantedAuthority> authoritiesAdmin = new ArrayList<>();
    Collection<GrantedAuthority> authoritiesUser = new ArrayList<>();
    authoritiesAdmin.add(new SimpleGrantedAuthority("ADMIN"));
    authoritiesUser.add(new SimpleGrantedAuthority("USER"));
    AppUserPrincipal appUserPrincipalAdmin = new AppUserPrincipal(userAdmin);
    AppUserPrincipal appUserPrincipalUser = new AppUserPrincipal(userUser);
    assertEquals(authoritiesAdmin,appUserPrincipalAdmin.getAuthorities());
    assertEquals(authoritiesUser,appUserPrincipalUser.getAuthorities());

  }
}
