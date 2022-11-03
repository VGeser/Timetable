package ru.nsu.timetable.backend.config

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.security.SecurityScheme
import io.swagger.v3.oas.models.Components

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.security.SecurityRequirement
//import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
@SecurityScheme(
    name = "token",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer"
)
class OpenApiConfig  {
//    @Bean
//    fun customizeOpenAPI(): OpenAPI {
//        val securitySchemeName = "token"
//        return OpenAPI()
//            .addSecurityItem(
//                SecurityRequirement()
//                    .addList(securitySchemeName)
//            )
//            .components(
//                Components()
//                    .addSecuritySchemes(
//                        securitySchemeName, SecurityScheme()
//                            .name(securitySchemeName)
//                            .type(SecurityScheme.Type.HTTP)
//                            .scheme("bearer")
//                            .bearerFormat("JWT")
//                    )
//            )
//    }
}