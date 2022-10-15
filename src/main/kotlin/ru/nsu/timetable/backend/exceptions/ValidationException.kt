package ru.nsu.timetable.backend.exceptions

import org.springframework.http.HttpStatus

class ValidationException(message: String) : BaseException(message, HttpStatus.CONFLICT)