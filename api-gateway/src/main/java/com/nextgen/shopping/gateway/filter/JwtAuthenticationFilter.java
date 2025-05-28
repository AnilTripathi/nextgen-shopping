package com.nextgen.shopping.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final ObjectMapper objectMapper;
    private final SecretKey key;
    private final List<String> excludedUrls;

    public JwtAuthenticationFilter(
            ObjectMapper objectMapper,
            @Value("${security.jwt.secret}") String secret,
            @Value("#{'${security.jwt.excluded-urls:/auth/**,/swagger-ui/**,/v3/api-docs/**,/actuator/**}'.split(',')}") List<String> excludedUrls
    ) {
        this.objectMapper = objectMapper;
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.excludedUrls = excludedUrls;
        log.info("JWT Authentication Filter initialized with {} excluded URLs", excludedUrls.size());
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        // Skip authentication for excluded URLs
        if (isExcludedUrl(path)) {
            log.debug("Skipping JWT authentication for excluded URL: {}", path);
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or invalid authorization header for path: {}", path);
            return onError(exchange, "Missing or invalid authorization header", HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(7);
        try {
            Claims claims = validateAndExtractClaims(token);
            log.debug("JWT validated successfully for user: {}", claims.getSubject());
            
            ServerHttpRequest mutatedRequest = mutateRequestWithClaims(request, claims);
            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        } catch (ExpiredJwtException e) {
            log.warn("JWT token has expired: {}", e.getMessage());
            return onError(exchange, "JWT token has expired", HttpStatus.UNAUTHORIZED);
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT token: {}", e.getMessage());
            return onError(exchange, "Unsupported JWT token", HttpStatus.UNAUTHORIZED);
        } catch (MalformedJwtException e) {
            log.warn("Malformed JWT token: {}", e.getMessage());
            return onError(exchange, "Malformed JWT token", HttpStatus.UNAUTHORIZED);
        } catch (SignatureException e) {
            log.warn("Invalid JWT signature: {}", e.getMessage());
            return onError(exchange, "Invalid JWT signature", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            log.error("Error processing JWT token", e);
            return onError(exchange, "Invalid JWT token", HttpStatus.UNAUTHORIZED);
        }
    }

    private boolean isExcludedUrl(String path) {
        return excludedUrls.stream().anyMatch(pattern -> {
            if (pattern.endsWith("/**")) {
                String prefix = pattern.substring(0, pattern.length() - 3);
                return path.startsWith(prefix);
            }
            return path.equals(pattern);
        });
    }

    private Claims validateAndExtractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private ServerHttpRequest mutateRequestWithClaims(ServerHttpRequest request, Claims claims) {
        return request.mutate()
                .header("X-Auth-User-Id", String.valueOf(claims.get("userId", String.class)))
                .header("X-Auth-Username", claims.getSubject())
                .header("X-Auth-Roles", String.valueOf(claims.get("roles", List.class)))
                .build();
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> errorResponse = Map.of(
                "error", message,
                "status", String.valueOf(status.value()),
                "path", exchange.getRequest().getPath().value()
        );

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(errorResponse);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            log.error("Error writing error response", e);
            return response.setComplete();
        }
    }

    @Override
    public int getOrder() {
        return -1; // Execute before other filters
    }
} 