package ru.nsu.timetable.backend.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.nsu.timetable.backend.dto.auth.LoginRequestDto
import ru.nsu.timetable.backend.dto.auth.LoginResponseDto
import ru.nsu.timetable.backend.dto.auth.RegisterUserDto
import ru.nsu.timetable.backend.entity.User
import ru.nsu.timetable.backend.exceptions.InvalidLoginOrPasswordException
import ru.nsu.timetable.backend.security.jwt.JwtTokenProvider
import ru.nsu.timetable.backend.service.UserService
import java.security.Principal


@RestController
@RequestMapping("api/v1")
class AuthController(
    val userService: UserService,
    val tokenProvider: JwtTokenProvider
) {
    @PostMapping("register")
    fun registerUser(@RequestBody request: RegisterUserDto){
        userService.registerUser(request.username, request.name, request.password)
    }

    @PostMapping("login")
    fun loginUser(@RequestBody request: LoginRequestDto): LoginResponseDto{
        val user = userService.findByUsernameAndPassword(request.username, request.password)?: throw InvalidLoginOrPasswordException()
        val (token, _) = tokenProvider.createToken(username = user.username, role = user.role)
        return LoginResponseDto(
            username = user.username,
            token = token
        )
    }

    @PostMapping("checkAuth")
    fun checkAuth(principal: UsernamePasswordAuthenticationToken): ResponseEntity<String>{
        return ResponseEntity((principal.principal as User).name, HttpStatus.OK)
    }

}