package com.example.banking.jwt;

import com.example.banking.model.User;
import com.example.banking.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.*;

public class UserAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    private static final Logger logger = LoggerFactory.getLogger(UserAuthenticationConverter.class);
    private static final String DEFAULT_AUTHORITY_PREFIX = "ROLE_";
    private static final String DEFAULT_AUTHORITIES_CLAIM_DELIMITER = " ";
    private static final Collection<String> WELL_KNOWN_AUTHORITIES_CLAIM_NAMES = Arrays.asList("scope", "scp");
    private String subjectClaimName = "sub";
    private String rolesClaimName = "realm_access.roles";
    private String authoritiesClaimName;
    private final UserService userService;

    public UserAuthenticationConverter(UserService userService) {
        this.userService = userService;
    }

    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt jwt) {
        Collection<GrantedAuthority> authorities = this.convertRolesToAuthorities(jwt);
        String subjectClaimValue = jwt.getClaimAsString(this.subjectClaimName);
        Optional<User> existingUser = userService.findByIdentity(subjectClaimValue);
        User user;
        if (existingUser.isEmpty()) {
            user = new User();
            user.setIdentity(subjectClaimValue);
            user.setUsername(subjectClaimValue);
            user.setRoles(new HashSet<>());
            user = userService.create(user);
        } else {
            user = existingUser.get();
        }
        return new UserJwtAuthenticationToken(jwt, user, authorities);
    }

    private Collection<GrantedAuthority> convertRolesToAuthorities(Jwt jwt) {
        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        for (String authority : getAuthorities(jwt)) {
            grantedAuthorities.add(new SimpleGrantedAuthority(DEFAULT_AUTHORITY_PREFIX + authority));
        }
        return grantedAuthorities;
    }

    private Collection<String> getAuthorities(Jwt jwt) {
        String claimName = getAuthoritiesClaimName(jwt);
        if (claimName == null) {
            logger.trace("Returning no authorities since could not find any claims that might contain scopes");
            return Collections.emptyList();
        }
        if (logger.isTraceEnabled()) {
            logger.trace("Looking for scopes in claim {}", claimName);
        }
        Object authorities = jwt.getClaim(claimName);
        if (authorities instanceof String) {
            if (StringUtils.hasText((String) authorities)) {
                return Arrays.asList(((String) authorities).split(DEFAULT_AUTHORITIES_CLAIM_DELIMITER));
            }
            return Collections.emptyList();
        }
        if (authorities instanceof Collection) {
            return castAuthoritiesToCollection(authorities);
        }
        return Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    private Collection<String> castAuthoritiesToCollection(Object authorities) {
        return (Collection<String>) authorities;
    }

    private String getAuthoritiesClaimName(Jwt jwt) {
        if (this.authoritiesClaimName != null) {
            return this.authoritiesClaimName;
        }
        for (String claimName : WELL_KNOWN_AUTHORITIES_CLAIM_NAMES) {
            if (jwt.hasClaim(claimName)) {
                return claimName;
            }
        }
        return null;
    }

    public void setSubjectClaimName(String subjectClaimName) {
        Assert.hasText(subjectClaimName, "principalClaimName cannot be empty");
        this.subjectClaimName = subjectClaimName;
    }

    public void setAuthoritiesClaimName(String authoritiesClaimName) {
        this.authoritiesClaimName = authoritiesClaimName;
    }
}
