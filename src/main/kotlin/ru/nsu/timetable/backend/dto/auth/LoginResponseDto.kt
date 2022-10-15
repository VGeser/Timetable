package ru.nsu.timetable.backend.dto.auth

data class LoginResponseDto(
    val username: String,
    val token: String,
)