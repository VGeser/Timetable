package ru.nsu.timetable.backend.controller.tte

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*
import ru.nsu.timetable.backend.entity.Room
import ru.nsu.timetable.backend.repo.SlotRepository
import ru.nsu.timetable.backend.repo.RoomRepository
import ru.nsu.timetable.backend.repo.slotSet


data class RoomDto(
    val name: String,
    val capacity: Int,
    val tools: Boolean,
    val slots: List<Long>
)

@RequestMapping("/api/v1/rooms")
@RestController
@SecurityRequirement(name = "token")
@Tag(name = "2. Room controller")
class RoomController(val repo: RoomRepository, val slotRepo: SlotRepository) {
    @Operation(summary = "Create room")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Room was successfully created")
        ]
    )
    @PostMapping("")
    @Secured("ROLE_ADMIN", "ROLE_DISPATCHER")
    fun post(
        @RequestBody data: RoomDto
    ): Room {
        return Room(
            name = data.name,
            capacity = data.capacity,
            tools = data.tools,
            availableSlots = slotRepo.slotSet(data.slots)
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
        return repo.findAllActive()
    }

    @Operation(summary = "Update room information")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Update successful"),
            ApiResponse(responseCode = "404", description = "Room with id does not exist")
        ]
    )
    @Secured("ROLE_ADMIN", "ROLE_DISPATCHER")
    @PatchMapping("/{id}")
    fun patch(
        @PathVariable id: Long,
        @RequestBody data: RoomDto,
    ): Room {
        val room = repo.getReferenceById(id)
        room.name = data.name
        room.capacity = data.capacity
        room.tools = data.tools
        room.availableSlots = slotRepo.slotSet(data.slots)
        repo.save(room)
        return room
    }

    @Operation(summary = "Delete room")
    @ApiResponses( value = [
        ApiResponse(responseCode = "200", description = "Success"),
        ApiResponse(responseCode = "404", description = "Room not found")
    ])
    @Secured("ROLE_ADMIN", "ROLE_DISPATCHER")
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long){
        repo.getReferenceById(id).apply { active = false }.let { repo.save(it) }
    }
}
