// src/main/java/com/app/HotelBooking/config/OpenApiConfig.java
package com.app.HotelBooking.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
  @Bean
  public OpenAPI customOpenAPI() {
    final String SCHEME_NAME = "bearer-jwt";
    return new OpenAPI()
      .addSecurityItem(new SecurityRequirement().addList(SCHEME_NAME))
      .components(new Components()
        .addSecuritySchemes(SCHEME_NAME,
          new SecurityScheme()
            .name("Authorization")
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
        )
      );
  }
}
