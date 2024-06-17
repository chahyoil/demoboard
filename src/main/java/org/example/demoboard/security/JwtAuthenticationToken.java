package org.example.demoboard.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;


/*
    * JwtAuthenticationToken class is responsible for creating JWT authentication tokens.
 */
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final UserDetails principal;
    private final String accessToken;

    public JwtAuthenticationToken(UserDetails principal, String accessToken, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.accessToken = accessToken;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return accessToken;
    }

    @Override
    public UserDetails getPrincipal() {
        return principal;
    }
}