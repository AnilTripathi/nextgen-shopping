package com.nextgen.shopping.gateway.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@Order(-2) // Execute before DefaultErrorWebExceptionHandler
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final ObjectMapper objectMapper;

    public GlobalExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        if (ex instanceof JwtAuthenticationException) {
            return handleJwtAuthenticationException(exchange, (JwtAuthenticationException) ex);
        } else if (ex instanceof ExpiredJwtException) {
            return handleJwtError(exchange, "JWT token has expired", HttpStatus.UNAUTHORIZED);
        } else if (ex instanceof UnsupportedJwtException) {
            return handleJwtError(exchange, "Unsupported JWT token", HttpStatus.UNAUTHORIZED);
        } else if (ex instanceof MalformedJwtException) {
            return handleJwtError(exchange, "Malformed JWT token", HttpStatus.UNAUTHORIZED);
        } else if (ex instanceof SignatureException) {
            return handleJwtError(exchange, "Invalid JWT signature", HttpStatus.UNAUTHORIZED);
        } else {
            log.error("Unhandled exception", ex);
            return handleJwtError(exchange, "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Mono<Void> handleJwtAuthenticationException(ServerWebExchange exchange, JwtAuthenticationException ex) {
        return handleJwtError(exchange, ex.getMessage(), ex.getStatus());
    }

    private Mono<Void> handleJwtError(ServerWebExchange exchange, String message, HttpStatus status) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> errorResponse = Map.of(
                "error", message,
                "status", String.valueOf(status.value())
        );

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(errorResponse);
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
            return exchange.getResponse().writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            log.error("Error writing error response", e);
            return exchange.getResponse().setComplete();
        }
    }
} 