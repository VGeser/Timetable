package ru.nsu.timetable.backend.dto.auth

data class RegisterUserDto(
    val username:String,
    val name:String,
    val password:String,
)