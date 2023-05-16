package ru.nsu.timetable.backend.controller.tte

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpClientErrorException.NotFound
import ru.nsu.timetable.backend.entity.Course
import ru.nsu.timetable.backend.entity.Group
import ru.nsu.timetable.backend.repo.CourseRepository
import ru.nsu.timetable.backend.repo.SlotRepository
import ru.nsu.timetable.backend.repo.GroupRepository
import ru.nsu.timetable.backend.repo.slotSet


data class GroupDto(
    val name: String,
    val quantity: Long,
    val slots: List<Long>,
    val courses: List<Long>,
)


@RequestMapping("/api/v1/groups")
@RestController
@SecurityRequirement(name = "token")
@Tag(name = "4. Group controller")
class GroupController(
    val repo: GroupRepository,
    val slotRepo: SlotRepository,
    val courseRepo: CourseRepository,
) {
    @Operation(summary = "Create group")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Group was successfully created")
        ]
    )
    @PostMapping("")
    fun post(@RequestBody data: GroupDto): Group {

        return Group(
            name = data.name,
            availableSlots = slotRepo.slotSet(data.slots),
            quantity = data.quantity.toByte(),
            courses = getCourses(data.courses).toSet()
        ).let { repo.save(it) }
    }


    @Operation(summary = "Get group by id")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Success"),
            ApiResponse(responseCode = "404", description = "Group with id does not exist")
        ]
    )
    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): Group {
        return repo.getReferenceById(id)
    }


    @Operation(summary = "Get all groups")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Success"),
        ]
    )
    @GetMapping("")
    fun getAll(): List<Group> {
        return repo.findAllActive()
    }

    @Operation(summary = "Update group information")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Update successful"),
            ApiResponse(responseCode = "404", description = "Group with id does not exist")
        ]
    )
    @PatchMapping("/{id}")
    fun patch(@PathVariable id: Long, @RequestBody data: GroupDto): Group {
        val group = repo.getReferenceById(id)
        group.name = data.name
        group.availableSlots = slotRepo.slotSet(data.slots)
        group.quantity = data.quantity.toByte()
        group.courses = getCourses(data.courses).toSet()
        repo.save(group)
        return group
    }

    @Operation(summary = "Delete group")
    @ApiResponses( value = [
        ApiResponse(responseCode = "200", description = "Success"),
        ApiResponse(responseCode = "404", description = "Group not found")
    ])
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long){
        repo.getReferenceById(id).apply { active = false }.let { repo.save(it) }
    }


    private fun getCourses(ids: List<Long>): List<Course>{
        val dbCourses = courseRepo.findAllById(ids)
        if(dbCourses.size != ids.size){
            throw HttpClientErrorException(HttpStatus.NOT_FOUND, "Some courses do not exist")
        }
        return dbCourses
    }
}