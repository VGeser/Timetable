package ru.nsu.timetable.backend.dto.auth

data class CheckAuthDto(
    val username: String,
    val role: String,
)
