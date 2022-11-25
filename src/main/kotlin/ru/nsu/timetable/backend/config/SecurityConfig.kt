package ru.nsu.timetable.backend.config

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.SecurityConfigurerAdapter
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import ru.nsu.timetable.backend.security.jwt.JwtConfigurer
import ru.nsu.timetable.backend.security.jwt.JwtTokenProvider

@Configuration
class SecurityConfig(private val jwtTokenProvider: JwtTokenProvider): WebSecurityConfigurerAdapter() {
    override fun configure(http: HttpSecurity) {
        http
            .httpBasic().disable()
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            .antMatchers("/api/v1/login").permitAll()
            .antMatchers("/api/v1/register").permitAll()
            .antMatchers("/api/docs").permitAll()
            .antMatchers("/api/docs/swagger-config").permitAll()
            .antMatchers("/swagger/**").permitAll()
            .anyRequest().authenticated()
            .and()
            .apply<SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity>>(JwtConfigurer(jwtTokenProvider))
    }
}

@Configuration
class WebConfiguration : WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**").allowedMethods("*")
    }
}