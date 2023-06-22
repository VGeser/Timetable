package ru.nsu.timetable.backend.controller

import io.swagger.v3.oas.annotations.Operation
import org.springframework.security.access.annotation.Secured
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.nsu.timetable.backend.entity.Role
import ru.nsu.timetable.backend.entity.User
import ru.nsu.timetable.backend.exceptions.ValidationException
import ru.nsu.timetable.backend.repo.UserRepository


data class UserDTO(
    val username: String,
    val name: String,
    val role: Role,
)

@RestController
@RequestMapping("api/v1/users")
@Secured("ROLE_ADMIN")
class UsersController(
    val repo: UserRepository
){
    @GetMapping()
    @Operation(summary = "Get all users")
    fun getAllUsers(): List<UserDTO>{
        return repo.findAll().map {
            UserDTO(
                it.username,
                it.name,
                it.role
            )
        }
    }

    @DeleteMapping("{username}")
    @Operation(summary = "Delete user by username")
    fun deleteUser(@PathVariable username: String, principal: UsernamePasswordAuthenticationToken){
        if(username == (principal.principal as User).username){
            throw ValidationException("You cannot delete yourself")
        }
        val user = repo.getByUsername(username)
        repo.delete(user)
    }
}