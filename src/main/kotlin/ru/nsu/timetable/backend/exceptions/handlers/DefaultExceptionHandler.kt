package ru.nsu.timetable.backend.exceptions.handlers

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import ru.nsu.timetable.backend.dto.ExceptionDto
import ru.nsu.timetable.backend.exceptions.BaseException

@ControllerAdvice
class DefaultExceptionHandler {
    @ExceptionHandler(
        value = [
            Exception::class,
            RuntimeException::class,
        ]
    )
    fun handleException(
        exception: Exception,
        request: WebRequest,
    ): ResponseEntity<ExceptionDto>{
        val status = (exception as? BaseException)?.httpStatus ?: HttpStatus.INTERNAL_SERVER_ERROR
        return ResponseEntity
            .status(status)
            .body(
                ExceptionDto(exception.message ?: "null")
            )
    }
}