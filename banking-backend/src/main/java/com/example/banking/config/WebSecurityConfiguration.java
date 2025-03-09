package com.example.banking.config;

import com.example.banking.jwt.UserAuthenticationConverter;
import com.example.banking.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {

    private final UserService userService;

    public WebSecurityConfiguration(UserService userService) {
        this.userService = userService;
    }

    @Bean
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers(
                                "/api/transactions/**",
                                "/api/accounts/**",
                                "/api/statements/**",
                                "/api/users/**").hasRole("USER")
                        .anyRequest().authenticated()
                )
                .sessionManagement(m -> m.sessionCreationPolicy(STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .oauth2ResourceServer(r -> r.jwt(
                        j -> j.jwtAuthenticationConverter(userAuthenticationConverter()))
                );

        return http.build();
    }

    @Bean
    public UserAuthenticationConverter userAuthenticationConverter() {
        var userAuthenticationConverter = new UserAuthenticationConverter(userService);
        userAuthenticationConverter.setAuthoritiesClaimName("realm_access.roles");
        return userAuthenticationConverter;
    }
}
