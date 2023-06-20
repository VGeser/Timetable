package ru.nsu.timetable.backend.controller

import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/v1/roletest")
class RoleTestController {
    @GetMapping("all")
//    @Secured()
    fun allowAll() = "Hello all"

    @GetMapping("dispatcher")
    @Secured("ROLE_DISPATCHER")
    fun allowDispatcher() = "Hello dispatcher"

    @GetMapping("admin")
    @Secured("ROLE_ADMIN")
    fun allowAdmin() = "Hello admin"
}