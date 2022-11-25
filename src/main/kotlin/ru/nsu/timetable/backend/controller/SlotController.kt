package ru.nsu.timetable.backend.controller

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.nsu.timetable.backend.entity.Slot
import ru.nsu.timetable.backend.service.SlotService


@RequestMapping("/api/v1/slots")
@SecurityRequirement(name = "token")
@RestController
class SlotController(val service: SlotService) {
    @GetMapping
    fun getSlots(): List<Slot> = service.getSlots()
}