package ru.nsu.timetable.backend

import io.swagger.v3.core.converter.AnnotatedType
import io.swagger.v3.core.converter.ModelConverter
import io.swagger.v3.core.converter.ModelConverterContext
import io.swagger.v3.core.util.Json
import io.swagger.v3.oas.models.media.Schema
import org.springdoc.core.converters.PropertyCustomizingConverter
import org.springdoc.core.customizers.PropertyCustomizer
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import java.util.*

@SpringBootApplication
class TimetableApplication




fun main(args: Array<String>) {
    runApplication<TimetableApplication>(*args)
}
