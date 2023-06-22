package ru.nsu.timetable.backend.config

//import org.springframework.security.config.annotation.web.configuration.
// WebSecurityConfigurerAdapter

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.SecurityConfigurerAdapter
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import ru.nsu.timetable.backend.security.jwt.JwtConfigurer
import ru.nsu.timetable.backend.security.jwt.JwtTokenFilter
import ru.nsu.timetable.backend.security.jwt.JwtTokenProvider


@Configuration
class SecurityConfig(
    private val jwtTokenProvider: JwtTokenProvider,
    @Qualifier("FCK")
    private val authenticationEntryPoint: DelegatedAuthenticationEntryPoint,
    val filter: JwtTokenFilter
    ){
    @Bean
    fun configure(http: HttpSecurity): SecurityFilterChain {
        http
            .cors().and()
            .httpBasic().disable()
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            .requestMatchers("/api/v1/login").permitAll()
            .requestMatchers("/api/v1/register").permitAll()
            .requestMatchers("/api/docs").permitAll()
            .requestMatchers("/api/docs/swagger-config").permitAll()
            .requestMatchers("/swagger/**").permitAll()
            .requestMatchers("/api/v1/dropdb").permitAll()
            .requestMatchers(HttpMethod.GET,
                "/api/v1/courses/**"
                ).permitAll()
            .requestMatchers(HttpMethod.GET,
                "/api/v1/rooms/**"
            ).permitAll()
            .requestMatchers(HttpMethod.GET,
                "/api/v1/groups/**"
            ).permitAll()
            .requestMatchers(HttpMethod.GET,
                "/api/v1/teachers/**"
            ).permitAll()
            .requestMatchers(HttpMethod.GET,
                "/api/v1/courses/slots"
            ).permitAll()
            .requestMatchers(HttpMethod.GET,
                "/api/v1/table/**"
            ).permitAll()
            .requestMatchers(HttpMethod.GET,
                "/api/v1/slots"
            ).permitAll()
            .requestMatchers("/api/v1/socket").permitAll()
            .anyRequest().authenticated()
            .and()
            .apply<SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity>>(JwtConfigurer(jwtTokenProvider, filter))
            .and()
            .exceptionHandling()
            .authenticationEntryPoint(authenticationEntryPoint)
        return http.build()
    }
}

@Configuration
class WebConfiguration : WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**").allowedMethods("*")
    }
}


@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
class MethodSecurityConfig : GlobalMethodSecurityConfiguration()