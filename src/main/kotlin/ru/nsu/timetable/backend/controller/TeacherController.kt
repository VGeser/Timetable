package ru.nsu.timetable.backend.controller

//import org.springframework.web.bind.annotation.ResponseStatus
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.databind.node.ObjectNode
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.servlet.http.HttpServletRequest
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.core.MethodParameter
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.http.server.ServletServerHttpRequest
import org.springframework.stereotype.Service
import org.springframework.util.Assert
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter
import org.springframework.web.servlet.mvc.method.annotation.AbstractMessageConverterMethodProcessor
import ru.nsu.timetable.backend.entity.Teacher
import ru.nsu.timetable.backend.exceptions.BadRequestException
import ru.nsu.timetable.backend.repo.SlotRepository
import ru.nsu.timetable.backend.repo.TeacherRepository
import ru.nsu.timetable.backend.repo.slotSet


data class CreateTeacherDto(val name: String, val slots: List<Long>)

@RequestMapping("/api/v2/teachers")
@RestController
@SecurityRequirement(name = "token")
class TeacherController(val service: TeacherService) {
    @Operation(summary = "Create teacher")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Teacher was successfully created")
    ])
    @PostMapping("")
    fun post(@JsonArg("name") name: String, @JsonArg("slots") slots: List<Long>): Teacher{
        return service.create(name, slots)
    }
}

@Service
class TeacherService(val repo: TeacherRepository, val slotRepo: SlotRepository){
    fun create(name: String, slots: List<Long>): Teacher{
        return repo.save(Teacher(
                name=name,
                availableSlots = slotRepo.slotSet(slots)
        ))
    }
}

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class JsonArg(val name: String = "")

@Configuration
class WebConfig2(val mapper: ObjectMapper): WebMvcConfigurer{
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


        val result = try{
            arg.get(ann.name).let { mapper.treeToValue(it, parameter.parameterType) }
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