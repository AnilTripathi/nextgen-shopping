package com.nextgen.shopping.common.security.annotation;

import com.nextgen.shopping.common.security.config.AppSecurityConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(AppSecurityConfig.class)
public @interface EnableAppSecurity {
} 