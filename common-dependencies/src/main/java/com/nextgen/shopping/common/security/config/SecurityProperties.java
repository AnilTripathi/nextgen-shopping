package com.nextgen.shopping.common.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "security.whitelist")
public class SecurityProperties {
    private List<String> urls = new ArrayList<>(List.of(
        "/swagger-ui.html",
        "/swagger-ui/**",
        "/v3/api-docs/**",
        "/v3/api-docs",
        "/webjars/**",
        "/auth/**",
        "/register",
        "/login"
    ));
} 