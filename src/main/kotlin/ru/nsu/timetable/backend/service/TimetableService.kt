package ru.nsu.timetable.backend.service

import org.springframework.stereotype.Service
import ru.nsu.timetable.backend.entity.Slot
import ru.nsu.timetable.backend.entity.TimetableEntry
import ru.nsu.timetable.backend.repo.*


@Service
class RepoProvider(
    val slots: SlotRepository,
    val teachers: TeacherRepository,
    val courses: CourseRepository,
    val rooms: RoomRepository,
    val groups: GroupRepository,
)


abstract class TimetableGeneratorService(
    private val repos: RepoProvider,
) {
    abstract fun generate(): List<TimetableEntry>
}


class TimetableConflict(val priority: Int, val slot: Slot?, val message: String)

abstract class TimetableValidator(
    private val repos: RepoProvider,
) {
    abstract fun validate(): List<TimetableConflict>
}


class TimetableService(private val serviceImpl: TimetableGeneratorServiceImpl) {
    fun generateTimetable() {
    }

    fun validateTimetable() {

    }
}