package ru.nsu.timetable.backend.controller

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.web.bind.annotation.*
import ru.nsu.timetable.backend.dto.SlotDto
import ru.nsu.timetable.backend.dto.TimetableDto
import ru.nsu.timetable.backend.entity.TimetableEntry
import ru.nsu.timetable.backend.repo.RepoProvider
import ru.nsu.timetable.backend.repo.TimeTableRepository
import ru.nsu.timetable.backend.service.TimetableProblem
import ru.nsu.timetable.backend.service.TimetableService
import ru.nsu.timetable.backend.service.TimetableValidatorService


@RestController
@RequestMapping("api/v1/table")
@SecurityRequirement(name = "token")
class TimetableController(
  val  tableService: TimetableService,
  val repos: RepoProvider,
  val repo: TimeTableRepository,
    val validator: TimetableValidatorService,
) {
    @PostMapping("generate")
    fun generate(){
        tableService.generate()
    }

    @GetMapping("teacher/{id}")
    fun getTeacher(@PathVariable id: Long): TimetableDto{
        return tableToDto(tableService.getTeacherTimetable(repos.teachers.getReferenceById(id)))
    }

    @GetMapping("group/{id}")
    fun getGroup(@PathVariable id: Long): TimetableDto{
        return tableToDto(tableService.getGroupTimetable(repos.groups.getReferenceById(id)))
    }

    @GetMapping("room/{id}")
    fun getRoom(@PathVariable id: Long): TimetableDto{
        return tableToDto(tableService.getRoomTimetable(repos.rooms.getReferenceById(id)))
    }

    private fun tableToDto(table: List<TimetableEntry>): TimetableDto {
        // TODO: do not hardcode size
        val res: TimetableDto = Array(7) {Array(7) {null} }
        table.forEach { entry->
            val slot = SlotDto(
                entry.slot.id,
                entry.group.map { SlotDto.NameId(it.id, it.name) },
                SlotDto.NameId(entry.room.id, entry.room.name),
                SlotDto.NameId(entry.teacher.id, entry.teacher.name),
                SlotDto.NameId(entry.course.id, entry.course.name),
                entry.id
            )
            res[entry.slot.day.toInt()][entry.slot.slotRow.toInt()] = slot
        }
        return res
    }

    @GetMapping("problems")
    fun getProblems(): List<TimetableProblem>{
        return validator.validateCurrent()
    }

    @DeleteMapping("entry/{id}")
    fun deleteEntry(@PathVariable id: Long){
        repo.deleteById(id)
    }

    @PostMapping("entry/{id}/move")
    fun moveEntry(@PathVariable id: Long, @RequestBody data: MoveEntryDTO){
        val entry = repo.getReferenceById(id)
        entry.slot = repos.slots.getReferenceById(data.slot)
        repo.save(entry)
    }

    @PostMapping("entries")
    fun createEntry(@RequestBody data: CreateEntryDTO): TimetableEntry{
        return TimetableEntry(
            slot = repos.slots.getReferenceById(data.slot),
            teacher = repos.teachers.getReferenceById(data.teacher),
            course = repos.courses.getReferenceById(data.course),
            group = repos.groups.findAllById(data.groups).toSet(),
            room = repos.rooms.getReferenceById(data.room),
        ).let { repo.save(it) }
    }
}

data class MoveEntryDTO(
    val slot: Long,
)

data class CreateEntryDTO(
    val slot: Long,
    val room: Long,
    val groups: Set<Long>,
    val teacher: Long,
    val course: Long,
)