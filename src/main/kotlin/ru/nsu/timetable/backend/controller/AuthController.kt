package ru.nsu.timetable.backend.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.nsu.timetable.backend.dto.ExceptionDto
import ru.nsu.timetable.backend.dto.auth.CheckAuthDto
import ru.nsu.timetable.backend.dto.auth.LoginRequestDto
import ru.nsu.timetable.backend.dto.auth.LoginResponseDto
import ru.nsu.timetable.backend.dto.auth.RegisterUserDto
import ru.nsu.timetable.backend.entity.User
import ru.nsu.timetable.backend.exceptions.UnauthorizedException
import ru.nsu.timetable.backend.security.jwt.JwtTokenProvider
import ru.nsu.timetable.backend.service.UserService


@RestController
@RequestMapping("api/v1")
class AuthController(
    val userService: UserService,
    val tokenProvider: JwtTokenProvider
) {

    @PostMapping("register")
    @Operation(summary = "Register new user")
    @ApiResponses(
        ApiResponse(description = "User registered successfully", responseCode = "200"),
        ApiResponse(description = "User already exists", responseCode = "409"),
    )
    fun registerUser(@RequestBody request: RegisterUserDto) {
        userService.registerUser(request.username, request.name, request.password)
    }

    @PostMapping("login")
    @Operation(summary = "Get token for registered user")
    @ApiResponses(
        ApiResponse(description = "Login successful", responseCode = "200"),
        ApiResponse(
            description = "Invalid username or password",
            responseCode = "403",
            content = [Content(schema = Schema(implementation = ExceptionDto::class))]
        ),
    )
    fun loginUser(@RequestBody request: LoginRequestDto): LoginResponseDto {
        val user = userService.findByUsernameAndPassword(request.username, request.password)
            ?: throw UnauthorizedException("Invalid username or password")
        val (token, _) = tokenProvider.createToken(username = user.username, role = user.role)
        return LoginResponseDto(
            username = user.username,
            token = token
        )
    }

    @PostMapping("checkAuth")
    @SecurityRequirement(name = "token")
    @Operation(summary = "Check if your auth token is valid")
    fun checkAuth(principal: UsernamePasswordAuthenticationToken): CheckAuthDto {
        val user = principal.principal as User
        return CheckAuthDto(user.name, user.role.name)
    }
}