package ru.nsu.timetable.backend.controller

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.web.bind.annotation.*
import ru.nsu.timetable.backend.dto.SlotDto
import ru.nsu.timetable.backend.dto.TimetableDto
import ru.nsu.timetable.backend.entity.TimetableEntry
import ru.nsu.timetable.backend.repo.RepoProvider
import ru.nsu.timetable.backend.service.TimetableService


@RestController
@RequestMapping("api/v1/table")
@SecurityRequirement(name = "token")
class TimetableController(
  val  tableService: TimetableService,
  val repos: RepoProvider,
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
            )
            res[entry.slot.day.toInt()][entry.slot.slotRow.toInt()] = slot
        }
        return res
    }
}