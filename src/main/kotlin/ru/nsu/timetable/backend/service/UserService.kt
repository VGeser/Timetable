package ru.nsu.timetable.backend.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import ru.nsu.timetable.backend.entity.Role
import ru.nsu.timetable.backend.entity.User
import ru.nsu.timetable.backend.exceptions.ValidationException
import ru.nsu.timetable.backend.repo.UserRepository

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) {
    @Value("\${tt.admin.username}")
    lateinit var adminUsername: String

    @Value("\${tt.admin.password}")
    lateinit var adminPassword: String

    fun registerUser(username: String, name: String, password: String, role: Role = Role.DISPATCHER): User {
        if(userRepository.existsByUsername(username)){
            throw ValidationException("User already exists")
        }
        val user = User(
            username = username,
            name = name,
            password = passwordEncoder.encode(password),
            role = role,
        )
        userRepository.save(user)
        return user
    }

    fun findByUsernameAndPassword(username: String, password: String): User?{
        val user = userRepository.findByUsername(username)
        if(user == null){
            return null
        }
        if(passwordEncoder.matches(password, user.password)){
            return user
        }
        return null
    }

    @EventListener(ApplicationReadyEvent::class)
    fun createDefaultUser(){
        if(userRepository.findByUsername(adminUsername) == null){
            registerUser(adminUsername, "admin", adminPassword, Role.ADMIN)
        }
    }
}