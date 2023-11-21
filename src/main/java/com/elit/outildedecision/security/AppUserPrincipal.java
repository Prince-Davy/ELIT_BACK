
package com.elit.outildedecision.security;

import com.elit.outildedecision.entity.User;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@Data
public class AppUserPrincipal implements UserDetails {

  private static final long serialVersionUID = 1L;

  private final User user;

  /**
   * Constructs a new AppUserPrincipal object using a User object.
   *
   * @param user the User object to be used to create the principal.
   */
  public AppUserPrincipal(final User user) {
    this.user = user;
  }

  /**
   * Returns the username of the principal.
   *
   * @return the user email associated with the principal.
   */
  @Override
  public String getUsername() {
    if (this.user == null) {
      return null;
    }
    return user.getUserEmail();
  }

  /**
   * Returns the first name of the user associated with the principal.
   *
   * @return the first name of the user.
   */
  public String getUserFirstName() {
    return user.getUserFirstName();
  }

  /**
   * Returns the last name of the user associated with the principal.
   *
   * @return the last name of the user.
   */
  public String getUserLastName() {
    return user.getUserLastName();
  }

  /**
   * Returns true if the user account is not expired.
   *
   * @return true, as the user account never expires.
   */
  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  /**
   * Returns true if the user account is not locked.
   *
   * @return true, as the user account is never locked.
   */
  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  /**
   * Returns true if the user credentials are not expired.
   *
   * @return true, as the user credentials never expire.
   */
  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  /**
   * Returns true if the user is enabled.
   *
   * @return true, as the user is always enabled.
   */
  @Override
  public boolean isEnabled() {
    return true;
  }

  /**
   * Returns the password of the user associated with the principal.
   *
   * @return the password of the user.
   */
  @Override
  public String getPassword() {
    if (this.user == null) {
      return null;
    }
    return user.getUserPassword();
  }

  /**
   * Returns a collection of granted authorities for the user associated with the principal.
   *
   * @return a collection of granted authorities.
   */
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    Collection<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority(Boolean.TRUE.equals(user.getIsAdmin()) ? "ADMIN" : "USER"));
    return authorities;
  }
}

