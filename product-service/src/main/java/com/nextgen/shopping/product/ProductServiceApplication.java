package com.nextgen.shopping.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import com.nextgen.shopping.common.security.annotation.EnableAppSecurity;

@SpringBootApplication
@EnableDiscoveryClient
@EnableAppSecurity
public class ProductServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }
} 