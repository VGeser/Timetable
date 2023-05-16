package ru.nsu.timetable.backend.config

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.security.SecurityScheme
import io.swagger.v3.oas.models.Components

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.tags.Tag
import org.springdoc.core.customizers.OpenApiCustomizer
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
    @Bean
    fun customizeOpenAPI(): OpenApiCustomizer {
        return OpenApiCustomizer { openApi ->
            val tags = openApi.paths.values.map { it.readOperations().map { op -> op.tags }.flatten() }.flatten().toSet()
            openApi.tags = tags.map { Tag().name(it) }.sortedBy { it.name }
//            openApi.
        }
    }
}