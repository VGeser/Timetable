package ru.nsu.timetable.backend.service

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
    fun registerUser(username: String, name: String, password: String): User {
        if(userRepository.existsByUsername(username)){
            throw ValidationException("User already exists")
        }
        val user = User(
            username = username,
            name = name,
            password = passwordEncoder.encode(password),
            role = Role.DISPATCHER
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
}