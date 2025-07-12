package com.enroll.merchantN.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CustomJwtAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${client.allowed-user}")
    private String keycloakUser;

    @Override
    public Collection<GrantedAuthority> convert(Jwt source) {
        String username = source.getClaim("preferred_username");
        if (username == null || !username.equals(keycloakUser)) {
            throw new RuntimeException("Invalid username");
        }
        Map<String, Object> realmAccess = source.getClaim("realm_access");
        if (realmAccess == null || !realmAccess.containsKey("roles")) {
            throw new RuntimeException("No roles found in realm_access claim");
        }
        List<String> roles = (List<String>) realmAccess.get("roles");
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());
    }


}
