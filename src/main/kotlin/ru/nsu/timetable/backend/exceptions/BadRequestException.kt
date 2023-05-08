package ru.nsu.timetable.backend.exceptions

import org.springframework.http.HttpStatus

class BadRequestException(message: String): BaseException(message, HttpStatus.BAD_REQUEST)