package ru.nsu.timetable.backend.service

import org.springframework.stereotype.Service
import ru.nsu.timetable.backend.entity.Slot
import ru.nsu.timetable.backend.entity.TimetableEntry
import ru.nsu.timetable.backend.generator.EngineAdapter
import ru.nsu.timetable.backend.repo.*


@Service
class RepoProvider(
    val slots: SlotRepository,
    val teachers: TeacherRepository,
    val courses: CourseRepository,
    val rooms: RoomRepository,
    val groups: GroupRepository,
    val table: TimeTableRepository,
)


@Service
class TimetableService(private val repos: RepoProvider) {
    fun generate(){
        try {
            val table = EngineAdapter().getOutGenerated(repos)
            repos.table.deleteAll()
            table.forEach {
                repos.table.save(it)
            }
        }catch (e: NullPointerException){
            println("fuck")
        }

    }
}