package com.vsp.accidentManagement.services;

import com.vsp.accidentManagement.models.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class UserPrincipal implements UserDetails {

    private User user;

    public UserPrincipal(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Change this to return true instead of calling super
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Change this to return true instead of calling super
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Change this to return true instead of calling super
    }

    @Override
    public boolean isEnabled() {
        return true; // Change this to return true instead of calling super
    }

    // Getter for accessing the user object if needed
    public User getUser() {
        return user;
    }
}