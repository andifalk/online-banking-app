package com.example.banking.jwt;

import com.example.banking.model.User;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;

public class UserJwtAuthenticationToken extends AbstractAuthenticationToken {

    private final User user;
    private final Jwt jwt;

    public UserJwtAuthenticationToken(Jwt jwt, User user, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        setAuthenticated(true);
        this.user = user;
        this.jwt = jwt;
    }

    @Override
    public Object getCredentials() {
        return jwt;
    }

    @Override
    public Object getPrincipal() {
        return user;
    }
}
