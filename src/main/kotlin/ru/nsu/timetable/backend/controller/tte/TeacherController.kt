package ru.nsu.timetable.backend.controller.tte

//import org.springframework.web.bind.annotation.ResponseStatus
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*
import ru.nsu.timetable.backend.entity.Teacher
import ru.nsu.timetable.backend.repo.SlotRepository
import ru.nsu.timetable.backend.repo.TeacherRepository
import ru.nsu.timetable.backend.repo.slotSet


data class TeacherDto(
    val name: String,
    val slots: List<Long>,
)

@RequestMapping("/api/v1/teachers")
@RestController
@SecurityRequirement(name = "token")
@Tag(name = "1. Teacher controller")
class TeacherController(val repo: TeacherRepository, val slotRepo: SlotRepository) {
    @Operation(summary = "Create teacher")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Teacher was successfully created")
        ]
    )
    @PostMapping("")
    @Secured("ROLE_ADMIN", "ROLE_DISPATCHER")
    fun post(@RequestBody data: TeacherDto): Teacher {
        return Teacher(name = data.name, availableSlots = slotRepo.slotSet(data.slots)).let { repo.save(it) }
    }


    @Operation(summary = "Get teacher by id")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Success"),
            ApiResponse(responseCode = "404", description = "Teacher with id does not exist")
        ]
    )
    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): Teacher {
        return repo.getReferenceById(id)
    }


    @Operation(summary = "Get all teachers")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Success"),
        ]
    )
    @GetMapping("")
    fun getAll(): List<Teacher> {
        return repo.findAllActive()
    }

    @Operation(summary = "Update teacher information")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Update successful"),
            ApiResponse(responseCode = "404", description = "Teacher with id does not exist")
        ]
    )
    @Secured("ROLE_ADMIN", "ROLE_DISPATCHER")
    @PatchMapping("/{id}")
    fun patch(@PathVariable id: Long, @RequestBody data: TeacherDto): Teacher {
        val teacher = repo.getReferenceById(id)
        teacher.name = data.name
        teacher.availableSlots = slotRepo.slotSet(data.slots)
        repo.save(teacher)
        return teacher
    }

    @Operation(summary = "Delete teacher")
    @ApiResponses( value = [
        ApiResponse(responseCode = "200", description = "Success"),
        ApiResponse(responseCode = "404", description = "Teacher not found")
    ])
    @Secured("ROLE_ADMIN", "ROLE_DISPATCHER")
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long){
        repo.getReferenceById(id).apply { active = false }.let { repo.save(it) }
    }
}
