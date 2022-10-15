package ru.nsu.timetable.backend.exceptions

import org.springframework.http.HttpStatus

class InvalidLoginOrPasswordException
    : BaseException("invalid username or password", HttpStatus.FORBIDDEN)
