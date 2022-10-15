package ru.nsu.timetable.backend.dto.auth

data class LoginRequestDto(
    val username: String,
    val password: String,
)
