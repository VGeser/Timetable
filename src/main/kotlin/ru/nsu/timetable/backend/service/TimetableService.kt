package ru.nsu.timetable.backend.service

import org.springframework.stereotype.Service
import ru.nsu.timetable.backend.entity.*
import ru.nsu.timetable.backend.generator.EngineAdapter
import ru.nsu.timetable.backend.repo.*


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

    fun getGroupTimetable(group:Group): List<TimetableEntry>{
        return repos.table.findAllByGroup(group)
    }

    fun getTeacherTimetable(teacher: Teacher): List<TimetableEntry>{
        return repos.table.findAllByTeacher(teacher)
    }

    fun getRoomTimetable(room: Room): List<TimetableEntry> {
        return repos.table.findAllByRoom(room)
    }
}