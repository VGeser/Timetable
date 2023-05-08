package ru.nsu.timetable.backend.exceptions.handlers

import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.context.request.WebRequest
import ru.nsu.timetable.backend.dto.ExceptionDto
import ru.nsu.timetable.backend.exceptions.BadRequestException
import ru.nsu.timetable.backend.exceptions.BaseException
import ru.nsu.timetable.backend.exceptions.UnauthorizedException

@ControllerAdvice
class DefaultExceptionHandler {
    @ExceptionHandler(
        value = [
            Exception::class,
            RuntimeException::class,
        ]
    )
    @Order(99)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleException(
        exception: Exception,
        request: WebRequest,
    ): ResponseEntity<ExceptionDto>{
        return respond(exception.message, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(value = [BadRequestException::class])
    @Order(1)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleBadRequest(exception: BadRequestException, request: WebRequest):ResponseEntity<ExceptionDto>{
        return respond(exception.message, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(value = [UnauthorizedException::class])
    @Order(1)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleUnauthorized(exception: UnauthorizedException, request: WebRequest):ResponseEntity<ExceptionDto>{
        return respond(exception.message, HttpStatus.UNAUTHORIZED)
    }


    private fun respond(message: String?, code: HttpStatus): ResponseEntity<ExceptionDto>{
        return ResponseEntity.status(code).body(ExceptionDto(message?:"null"))
    }
}