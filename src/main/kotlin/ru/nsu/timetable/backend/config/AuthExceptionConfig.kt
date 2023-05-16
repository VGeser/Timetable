package ru.nsu.timetable.backend.config

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerExceptionResolver

@Component("FCK")
class DelegatedAuthenticationEntryPoint(
    @Qualifier("handlerExceptionResolver") val resolver: HandlerExceptionResolver
): AuthenticationEntryPoint{
    init {
        println("I WAS INITED!!!")
    }
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        resolver.resolveException(request, response, null, authException)
    }

}