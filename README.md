# NextGen Shopping Microservices

A modern, cloud-native e-commerce platform built with Spring Boot 3.4.6 microservices architecture.

## 🏗️ Architecture

The application is composed of the following microservices:

- **Config Server** (Port: 8888) - Centralized configuration management using native file system
- **Eureka Server** (Port: 8761) - Service discovery and registration
- **API Gateway** (Port: 8080) - Single entry point with JWT authentication and route management
- **Auth Service** - Authentication, authorization, and user management
- **Product Service** - Product catalog and inventory management
- **Cart Service** - Shopping cart operations and management
- **Vendor Service** - Vendor onboarding and management
- **Notification Service** - Asynchronous notification handling

## 🔐 Security

The platform implements a centralized JWT-based security mechanism:

- JWT token-based authentication with role-based access control
- Centralized security configuration via `common-dependencies`
- API Gateway level token validation and claim propagation
- Protected API endpoints with configurable public URL whitelist
- Support for token expiration and refresh mechanisms

### API Gateway Security Features
- JWT validation at gateway level
- Token claims propagation to microservices via headers:
  - X-Auth-User-Id
  - X-Auth-Username
  - X-Auth-Roles
- Configurable excluded URLs for public access

## 🚀 Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.8+
- PostgreSQL (for data storage)
- Docker (optional)

### Environment Variables

```bash
# Required
JWT_SECRET=your-256-bit-secret-key-here
CONFIG_SERVER_USERNAME=config
CONFIG_SERVER_PASSWORD=config
ENCRYPT_KEY=your-strong-encryption-key-here

# Optional
CONFIG_LOCATION=../shopping-config  # For config server
EUREKA_SERVER_URL=http://eureka:password@localhost:8761/eureka/
```

### Building the Project

```bash
mvn clean install
```

### Running the Services

Start the services in the following order:

1. Config Server
```bash
cd config-server
mvn spring-boot:run
```

2. Eureka Server
```bash
cd eureka-server
mvn spring-boot:run
```

3. Auth Service
```bash
cd auth-service
mvn spring-boot:run
```

4. Other Services
```bash
cd <service-name>
mvn spring-boot:run
```

## 📚 API Documentation

Each service exposes its API documentation via OpenAPI/Swagger:

- Gateway Aggregated APIs: `http://localhost:8080/swagger-ui.html`
- Individual Service APIs: `http://<service-host>:<port>/swagger-ui.html`
- OpenAPI Specs: `http://<service-host>:<port>/v3/api-docs`

### Available APIs

- **Auth Service**: User management and authentication
  - POST /auth/login
  - POST /auth/register
  - POST /auth/refresh-token

- **Product Service**: Product management
  - GET /api/products
  - GET /api/products/{id}
  - GET /api/products/vendor/{vendorId}
  - GET /api/products/category/{category}

- **Cart Service**: Shopping cart operations
  - GET /api/carts/{userId}
  - POST /api/carts/{userId}/items
  - PUT /api/carts/{userId}/items/{productId}
  - DELETE /api/carts/{userId}/items/{productId}

- **Vendor Service**: Vendor management
  - GET /api/vendors
  - GET /api/vendors/{id}
  - GET /api/vendors/status/{status}
  - GET /api/vendors/search

- **Notification Service**: Notification handling
  - POST /api/notifications
  - GET /api/notifications/recipient/{recipient}
  - GET /api/notifications/status/{status}
  - POST /api/notifications/retry-failed

## 🛠️ Common Dependencies

The `common-dependencies` module provides shared functionality:

- Security configurations with JWT support
- Exception handling with standardized responses
- OpenAPI/Swagger configuration with JWT security schemes
- Common DTOs and utility classes

### Using Common Dependencies

Add to your service's `pom.xml`:

```xml
<dependency>
    <groupId>com.nextgen.shopping</groupId>
    <artifactId>common-dependencies</artifactId>
    <version>${project.version}</version>
</dependency>
```

Enable security in your service:

```java
@SpringBootApplication
@EnableAppSecurity
public class ServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
    }
}
```

## 🔧 Configuration

Each service requires the following configuration in `application.yml`:

```yaml
spring:
  application:
    name: service-name
  cloud:
    config:
      uri: http://localhost:8888
      fail-fast: true
      username: ${CONFIG_SERVER_USERNAME:config}
      password: ${CONFIG_SERVER_PASSWORD:config}

security:
  jwt:
    secret: ${JWT_SECRET:your-256-bit-secret-key}
    expiration: 86400000 # 24 hours

  whitelist:
    urls:
      - /swagger-ui.html
      - /swagger-ui/**
      - /v3/api-docs/**
      - /v3/api-docs
      - /webjars/**
      - /auth/**
      - /register
      - /login
      - /actuator/**

eureka:
  client:
    service-url:
      defaultZone: ${EUREKA_SERVER_URL:http://eureka:password@localhost:8761/eureka/}
  instance:
    prefer-ip-address: true
```

## 📦 Technology Stack

- Spring Boot 3.4.6
- Spring Cloud (Config, Gateway, Netflix Eureka)
- Spring Security 6
- Spring Data JPA
- JWT Authentication
- OpenAPI/Swagger
- PostgreSQL
- Maven
- JUnit 5
- Docker (optional)

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details. 