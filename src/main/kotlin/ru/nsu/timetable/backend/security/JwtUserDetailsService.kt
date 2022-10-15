package ru.nsu.timetable.backend.security

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import ru.nsu.timetable.backend.repo.UserRepository
import ru.nsu.timetable.backend.security.jwt.JwtAuthenticationException

@Service
class JwtUserDetailsService(private val userRepository: UserRepository): UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        // TODO: replace with InvalidLoginOrPasswordException
        return userRepository.findByUsername(username) ?: throw JwtAuthenticationException(null)
    }
}