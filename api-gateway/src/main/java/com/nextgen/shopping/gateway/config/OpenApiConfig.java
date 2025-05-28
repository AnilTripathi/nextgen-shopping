package com.nextgen.shopping.gateway.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.core.properties.SwaggerUiConfigParameters;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Lazy;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    @Primary
    public OpenAPI api() {
        return new OpenAPI()
            .info(new Info()
                .title("NextGen Shopping Microservices API")
                .version("1.0")
                .description("API Documentation for NextGen Shopping Microservices"));
    }

    @Bean
    @Lazy(false)
    public List<GroupedOpenApi> apis(SwaggerUiConfigParameters swaggerUiParameters, RouteDefinitionLocator locator) {
        List<GroupedOpenApi> groups = new ArrayList<>();
        List<RouteDefinition> definitions = locator.getRouteDefinitions().collectList().block();
        
        // Add service groups
        if (definitions != null) {
            definitions.stream()
                .filter(routeDefinition -> routeDefinition.getId().matches(".*-service"))
                .forEach(routeDefinition -> {
                    String name = routeDefinition.getId();
                    String displayName = name.substring(0, 1).toUpperCase() + name.substring(1);
                    
                    swaggerUiParameters.addGroup(name);
                    groups.add(GroupedOpenApi.builder()
                        .pathsToMatch("/*/v3/api-docs/" + name)
                        .group(name)
                        .displayName(displayName)
                        .build());
                });
        }
        
        return groups;
    }
} 