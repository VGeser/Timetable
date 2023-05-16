package ru.nsu.timetable.backend.controller.tte

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*
import ru.nsu.timetable.backend.entity.Course
import ru.nsu.timetable.backend.entity.Teacher
import ru.nsu.timetable.backend.repo.CourseRepository
import ru.nsu.timetable.backend.repo.SlotRepository
import ru.nsu.timetable.backend.repo.TeacherRepository
import ru.nsu.timetable.backend.repo.slotSet

data class CourseDto(
    val name: String,
    val tools: Boolean,
    val frequency: Int,
    val teacher: Long,
    )


@RequestMapping("/api/v1/courses")
@RestController
@SecurityRequirement(name = "token")
@Tag(name = "3. Course controller")
class CourseController(
    val repo: CourseRepository,
    val slotRepo: SlotRepository,
    val teacherRepo: TeacherRepository,
) {
    @Operation(summary = "Create course")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Course was successfully created")
        ]
    )
    @PostMapping("")
    fun post(@RequestBody data: CourseDto): Course {
        return Course(
            name = data.name,
            tools = data.tools,
            frequency = data.frequency.toByte(),
            teacher = teacherRepo.getReferenceById(data.teacher)
        ).let { repo.save(it) }
    }


    @Operation(summary = "Get course by id")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Success"),
            ApiResponse(responseCode = "404", description = "Course with id does not exist")
        ]
    )
    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): Course {
        return repo.getReferenceById(id)
    }


    @Operation(summary = "Get all courses")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Success"),
        ]
    )
    @GetMapping("")
    fun getAll(): List<Course> {
        return repo.findAllActive()
    }

    @Operation(summary = "Update course information")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Update successful"),
            ApiResponse(responseCode = "404", description = "Course with id does not exist")
        ]
    )
    @PatchMapping("/{id}")
    fun patch(@PathVariable id: Long, @RequestBody data: CourseDto): Course {
        val course = repo.getReferenceById(id)
        course.name = data.name
        course.tools = data.tools
        course.teacher = teacherRepo.getReferenceById(data.teacher)
        course.frequency = data.frequency.toByte()
        repo.save(course)
        return course
    }

    @Operation(summary = "Delete course")
    @ApiResponses( value = [
        ApiResponse(responseCode = "200", description = "Success"),
        ApiResponse(responseCode = "404", description = "course not found")
    ])
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long){
        repo.getReferenceById(id).apply { active = false }.let { repo.save(it) }
    }
}
