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

    public AppUserPrincipal(User user) {
        this.user = user;
    }

    @Override
    public String getUsername() {
        return user.getUserEmail();// getUsername();
    }

    public String getUserFirstName() {
        return user.getUserFirstName();
    }

    public String getUserLastName() {
        return user.getUserLastName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getPassword() {
        return user.getUserPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        authorities.add(new SimpleGrantedAuthority(user.getIsAdmin() ? "ADMIN" : "USER"));
        return authorities;
    }
}
