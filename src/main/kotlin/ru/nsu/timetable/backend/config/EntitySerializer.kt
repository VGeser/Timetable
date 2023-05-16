package ru.nsu.timetable.backend.config

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.swagger.v3.core.converter.AnnotatedType
import io.swagger.v3.core.converter.ModelConverter
import io.swagger.v3.core.converter.ModelConverterContext
import io.swagger.v3.oas.models.media.ArraySchema
import io.swagger.v3.oas.models.media.NumberSchema
import io.swagger.v3.oas.models.media.Schema
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import ru.nsu.timetable.backend.entity.IdEntity

class EntitiesToIdsSerializer: JsonSerializer<Collection<IdEntity>>(){
    override fun serialize(value: Collection<IdEntity>, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeArray(value.map { it.id }.toLongArray(), 0, value.size)
    }
}

class EntityToIdSerializer: JsonSerializer<IdEntity>(){
    override fun serialize(value: IdEntity, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeNumber(value.id)
    }
}

@Configuration
class JsonSerializerConfig{
    @Bean()
    @Lazy(false)
    fun converter():EntityModelConverter {
        return EntityModelConverter()
    }
}

class EntityModelConverter: ModelConverter {
    override fun resolve(
        type: AnnotatedType,
        context: ModelConverterContext,
        chain: MutableIterator<ModelConverter>
    ): Schema<*> {
        val schema = chain.next().resolve(type, context, chain)
        if(type.ctxAnnotations == null){
            return schema
        }
        val ann = type.ctxAnnotations.filterIsInstance<JsonSerialize>().firstOrNull() ?: return schema
        if (ann.using == EntitiesToIdsSerializer::class){
            return ArraySchema().apply {
                items = NumberSchema()
            }
        }

        return when(ann.using){
            EntitiesToIdsSerializer::class -> ArraySchema().apply {
                items = NumberSchema()
            }
            EntityToIdSerializer::class -> NumberSchema()
            else -> schema
        }
    }
}