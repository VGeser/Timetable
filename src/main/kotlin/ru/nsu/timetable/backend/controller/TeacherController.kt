package ru.nsu.timetable.backend.controller

//import org.springframework.web.bind.annotation.ResponseStatus
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*
import ru.nsu.timetable.backend.config.JsonArg
import ru.nsu.timetable.backend.entity.Teacher
import ru.nsu.timetable.backend.repo.SlotRepository
import ru.nsu.timetable.backend.repo.TeacherRepository
import ru.nsu.timetable.backend.repo.slotSet


@RequestMapping("/api/v1/teachers")
@RestController
@SecurityRequirement(name = "token")
@Tag(name = "Teacher controller")
class TeacherController(val repo: TeacherRepository, val slotRepo: SlotRepository) {
    @Operation(summary = "Create teacher")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Teacher was successfully created")
        ]
    )
    @PostMapping("")
    fun post(@JsonArg("name") name: String, @JsonArg("slots") slots: List<Long>): Teacher {
        return Teacher(name = name, availableSlots = slotRepo.slotSet(slots)).let { repo.save(it) }
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
        return repo.findAll()
    }

    @Operation(summary = "Update teacher information")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Update successful"),
            ApiResponse(responseCode = "404", description = "Teacher with id does not exist")
        ]
    )
    @PatchMapping("/{id}")
    fun patch(@PathVariable id: Long, @JsonArg("name") name: String?, @JsonArg("slots") slots: List<Long>?): Teacher {
        val teacher = repo.getReferenceById(id)
        name?.let { teacher.name = it }
        slots?.let { teacher.availableSlots = slotRepo.slotSet(it) }
        repo.save(teacher)
        return teacher
    }

    @Operation(summary = "Delete teacher")
    @ApiResponses( value = [
        ApiResponse(responseCode = "200", description = "Success"),
        ApiResponse(responseCode = "404", description = "Teacher not found")
    ])
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long){
        repo.getReferenceById(id).apply { active = false }.let { repo.save(it) }
    }
}
