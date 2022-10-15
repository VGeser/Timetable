package ru.nsu.timetable.backend.repo

import org.springframework.data.jpa.repository.JpaRepository
import ru.nsu.timetable.backend.entity.User

interface UserRepository : JpaRepository<User, Long>{
    fun findByUsername(name: String): User?
    fun existsByUsername(name: String): Boolean
}