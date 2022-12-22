package ru.nsu.timetable.backend.controller

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.nsu.timetable.backend.service.TimetableService


@RestController
@RequestMapping("api/v1/table")
@SecurityRequirement(name = "token")
class TimetableController(
  val  tableService: TimetableService,
) {
    @PostMapping("generate")
    fun generate(){
        tableService.generate()
    }
}