package ru.nsu.timetable.backend.security.jwt

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.GenericFilterBean
import java.io.IOException
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerExceptionResolver

@Component
class JwtTokenFilter(private val jwtTokenProvider: JwtTokenProvider, @Qualifier("handlerExceptionResolver") val resolver: HandlerExceptionResolver) : GenericFilterBean() {
    @Throws(IOException::class, ServletException::class)
    override fun doFilter(req: ServletRequest, res: ServletResponse, filterChain: FilterChain) {
        try {
            val token = jwtTokenProvider.resolveToken(req as HttpServletRequest)
            if (token != null && jwtTokenProvider.validateToken(token)) {
                val auth = jwtTokenProvider.getAuthentication(token)
                if (auth != null) {
                    SecurityContextHolder.getContext().authentication = auth
                }
            }
            filterChain.doFilter(req, res)
        }catch (e: Exception){
            SecurityContextHolder.clearContext()
            resolver.resolveException(req as HttpServletRequest, res as HttpServletResponse, null, e)
        }
    }
}
// TODO: handle token absence