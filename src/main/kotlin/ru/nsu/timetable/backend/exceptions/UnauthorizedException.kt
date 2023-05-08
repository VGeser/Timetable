package ru.nsu.timetable.backend.exceptions

import org.springframework.http.HttpStatus

class UnauthorizedException(message: String)
    : BaseException(message, HttpStatus.FORBIDDEN)
