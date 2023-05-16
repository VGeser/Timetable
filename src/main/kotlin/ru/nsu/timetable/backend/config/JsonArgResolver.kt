package ru.nsu.timetable.backend.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.databind.node.ObjectNode
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import jakarta.servlet.http.HttpServletRequest
import org.springdoc.core.customizers.DataRestDelegatingMethodParameterCustomizer
import org.springdoc.core.customizers.DelegatingMethodParameterCustomizer
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.GenericTypeResolver
import org.springframework.core.MethodParameter
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.http.server.ServletServerHttpRequest
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.mvc.method.annotation.AbstractMessageConverterMethodProcessor
import ru.nsu.timetable.backend.exceptions.BadRequestException
//import java.util.*


@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
//@Parameter(`in` = ParameterIn.DEFAULT)
annotation class JsonArg(val name: String = "")

@Configuration
class WebConfig2(val mapper: ObjectMapper): WebMvcConfigurer {
    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(JsonArgResolver(listOf(MappingJackson2HttpMessageConverter(mapper)), mapper))
    }
}

class JsonArgResolver(converters: List<HttpMessageConverter<*>>, val mapper: ObjectMapper): AbstractMessageConverterMethodProcessor(converters){
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(JsonArg::class.java)
    }

    override fun resolveArgument(parameter: MethodParameter, mavContainer: ModelAndViewContainer?, webRequest: NativeWebRequest, binderFactory: WebDataBinderFactory?): Any? {
        val servletRequest = webRequest.getNativeRequest(HttpServletRequest::class.java) ?: throw IllegalArgumentException("No HttpServletRequest")

        val arg = if(servletRequest.attributeNames.asSequence().contains(PARSED_MESSAGE_ATTR)){
            servletRequest.getAttribute(PARSED_MESSAGE_ATTR) as ObjectNode
        }else {
            val inputMessage = ServletServerHttpRequest(servletRequest)
            (readWithMessageConverters<ObjectNode>(inputMessage, parameter, ObjectNode::class.java) as ObjectNode).also {
                servletRequest.setAttribute(PARSED_MESSAGE_ATTR, it)
            }
        }

        val expectedParameterNames = parameter.executable.parameters.filter { it.annotations.filterIsInstance<JsonArg>().isNotEmpty() }.map { it.getAnnotation(JsonArg::class.java).name }.toSet()
        println(expectedParameterNames)

        val unexpectedParameters = arg.fieldNames().asSequence().toSet().minus(expectedParameterNames)
        if (unexpectedParameters.isNotEmpty()){
            throw BadRequestException("Unexpected parameters: $unexpectedParameters")
        }

        val ann = parameter.getParameterAnnotation(JsonArg::class.java)!!
        val type = parameter.nestedGenericParameterType
        val jt = mapper.constructType(GenericTypeResolver.resolveType(type, parameter.containingClass))
//        mapper.readerFor(jt).readValue(arg.get(""))

        val result = try{
            arg.get(ann.name).let {
//                mapper.readValue("", parameter.parameterType)
                arg.get(ann.name)?.let { obj -> mapper.readerFor(jt).readValue<Any>(obj) }
            }
        } catch (e: MismatchedInputException){
            throw BadRequestException("Parameter '${ann.name}' has wrong type: should be '${e.targetType.simpleName}'")
        }

        if(!parameter.isOptional && result == null){
            throw BadRequestException("Required parameter '${ann.name}' is missing")
        }

        return result
    }

    override fun supportsReturnType(returnType: MethodParameter): Boolean {
        return true
    }

    override fun handleReturnValue(returnValue: Any?, returnType: MethodParameter, mavContainer: ModelAndViewContainer, webRequest: NativeWebRequest) {
        throw NotImplementedError()
    }

    companion object{
        const val PARSED_MESSAGE_ATTR = "LEADP_PARSED_MESSAGE_ATTR"
    }

}

@Configuration
class FuckThisShit{
    @Bean
    fun delegatingMethodParameterCustomizer(

    ): DelegatingMethodParameterCustomizer? {
        return Test()
    }
}

class Test: DelegatingMethodParameterCustomizer{
    override fun customize(originalParameter: MethodParameter, methodParameter: MethodParameter) {
        println("FUCK ME")
    }

}