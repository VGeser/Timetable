package ru.nsu.timetable.backend.exceptions

import org.springframework.http.HttpStatus

open class BaseException(
    message: String,
    val httpStatus: HttpStatus,
): RuntimeException(message)