# NextGen Shopping Microservices

A modern, cloud-native e-commerce platform built with Spring Boot 3.4.6 microservices architecture.

## 🏗️ Architecture

The application is composed of the following microservices:

- **Config Server** - Centralized configuration management
- **Eureka Server** - Service discovery and registration
- **API Gateway** - Single entry point for all client requests
- **Auth Service** - Handles authentication and authorization
- **Product Service** - Product catalog management
- **Cart Service** - Shopping cart operations
- **Vendor Service** - Vendor management
- **Notification Service** - Handles system notifications

## 🔐 Security

The platform implements a centralized JWT-based security mechanism:

- JWT token-based authentication
- Role-based access control
- Centralized security configuration via `common-dependencies`
- Protected API endpoints with whitelisted public URLs

## 🚀 Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.8+
- Docker (optional)

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

- Swagger UI: `http://<service-host>:<port>/swagger-ui.html`
- OpenAPI Spec: `http://<service-host>:<port>/v3/api-docs`

## 🛠️ Common Dependencies

The `common-dependencies` module provides shared functionality:

- Security configurations
- Exception handling
- Common DTOs
- OpenAPI configuration

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
security:
  jwt:
    secret: your-256-bit-secret-key
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
```

## 📦 Technology Stack

- Spring Boot 3.4.6
- Spring Cloud
- Spring Security 6
- JWT Authentication
- OpenAPI/Swagger
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