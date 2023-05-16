package ru.nsu.timetable.backend.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*
import ru.nsu.timetable.backend.config.JsonArg
import ru.nsu.timetable.backend.entity.Room
import ru.nsu.timetable.backend.repo.SlotRepository
import ru.nsu.timetable.backend.repo.RoomRepository
import ru.nsu.timetable.backend.repo.slotSet

@RequestMapping("/api/v1/rooms")
@RestController
@SecurityRequirement(name = "token")
@Tag(name = "Room controller")
class RoomController(val repo: RoomRepository, val slotRepo: SlotRepository) {
    @Operation(summary = "Create room")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Room was successfully created")
        ]
    )
    @PostMapping("")
    fun post(
        @JsonArg("name") name: String,
        @JsonArg("capacity") capacity: Int,
        @JsonArg("tools") hasTools: Boolean,
        @JsonArg("slots") slots: List<Long>,
    ): Room {
        return Room(
            name = name,
            capacity = capacity,
            tools = hasTools,
            availableSlots = slotRepo.slotSet(slots)
        ).let { repo.save(it) }
    }


    @Operation(summary = "Get room by id")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Success"),
            ApiResponse(responseCode = "404", description = "Room with id does not exist")
        ]
    )
    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): Room {
        return repo.getReferenceById(id)
    }


    @Operation(summary = "Get all rooms")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Success"),
        ]
    )
    @GetMapping("")
    fun getAll(): List<Room> {
        return repo.findAll()
    }

    @Operation(summary = "Update room information")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Update successful"),
            ApiResponse(responseCode = "404", description = "Room with id does not exist")
        ]
    )
    @PatchMapping("/{id}")
    fun patch(
        @PathVariable id: Long,
        @JsonArg("name") name: String?,
        @JsonArg("capacity") capacity: Int?,
        @JsonArg("tools") hasTools: Boolean?,
        @JsonArg("slots") slots: List<Long>?,
    ): Room {
        val room = repo.getReferenceById(id)
        name?.let { room.name = it }
        capacity?.let { room.capacity = capacity }
        hasTools?.let { room.tools = it }
        slots?.let { room.availableSlots = slotRepo.slotSet(it) }
        repo.save(room)
        return room
    }

    @Operation(summary = "Delete room")
    @ApiResponses( value = [
        ApiResponse(responseCode = "200", description = "Success"),
        ApiResponse(responseCode = "404", description = "Room not found")
    ])
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long){
        repo.getReferenceById(id).apply { active = false }.let { repo.save(it) }
    }
}
