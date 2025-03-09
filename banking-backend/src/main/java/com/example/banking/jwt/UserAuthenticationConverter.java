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
import org.springframework.util.StringUtils;

import java.util.*;

public class UserAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    private static final Logger logger = LoggerFactory.getLogger(UserAuthenticationConverter.class);
    private static final String DEFAULT_AUTHORITY_PREFIX = "ROLE_";
    private static final String DEFAULT_AUTHORITIES_CLAIM_DELIMITER = " ";
    private static final Collection<String> WELL_KNOWN_AUTHORITIES_CLAIM_NAMES = Arrays.asList("scope", "scp", "roles");
    private String authoritiesClaimName;
    private final UserService userService;

    public UserAuthenticationConverter(UserService userService) {
        this.userService = userService;
    }

    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt jwt) {
        Collection<GrantedAuthority> authorities = this.convertRolesToAuthorities(jwt);
        String subjectClaimValue = jwt.getSubject();
        Optional<User> existingUser = userService.findByIdentity(subjectClaimValue);
        User user;
        if (existingUser.isEmpty()) {
            user = new User();
            user.setIdentity(subjectClaimValue);
            user.setUsername(jwt.getClaimAsString("preferred_username"));
            user.setFirstName(jwt.getClaimAsString("given_name"));
            user.setLastName(jwt.getClaimAsString("family_name"));
            user.setEmail(jwt.getClaimAsString("email"));
            user.setRoles(new HashSet<>(getAuthorities(jwt)));
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

    @SuppressWarnings("unchecked")
    private Collection<String> getAuthorities(Jwt jwt) {
        String claimName = getAuthoritiesClaimName(jwt);
        if (claimName == null) {
            logger.debug("Returning no authorities since could not find any claims that might contain scopes");
            return Collections.emptyList();
        }
        if (logger.isTraceEnabled()) {
            logger.trace("Looking for authorities in claim {}", claimName);
        }

        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess == null || realmAccess.isEmpty()) {
            return Collections.emptyList();
        }
        if (realmAccess.containsKey("roles")) {
            Object roles = realmAccess.get("roles");
            if (roles instanceof Collection) {
                return (Collection<String>) roles;
            } else if (roles instanceof String) {
                if (StringUtils.hasText((String) roles)) {
                    return Arrays.asList(((String) roles).split(DEFAULT_AUTHORITIES_CLAIM_DELIMITER));
                } else {
                    return Collections.emptyList();
                }
            } else {
                return Collections.emptyList();
            }
        } else {
            return Collections.emptyList();
        }
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

    public void setAuthoritiesClaimName(String authoritiesClaimName) {
        this.authoritiesClaimName = authoritiesClaimName;
    }
}
