package ru.nsu.timetable.backend.security.jwt

import org.springframework.security.config.annotation.SecurityConfigurerAdapter
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


class JwtConfigurer(private val jwtTokenProvider: JwtTokenProvider, val filter: JwtTokenFilter) : SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity>() {
    override fun configure(httpSecurity: HttpSecurity) {
        val jwtTokenFilter = filter
        httpSecurity.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter::class.java)
    }
}
