package ru.nsu.timetable.backend.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.nsu.timetable.backend.repo.RepoProvider


@RestController
@RequestMapping("api/v1")
class DropDbController(
    val repos: RepoProvider,
) {
    @GetMapping("dropdb")
    fun dropdb(){
        repos.table.deleteAll()
        repos.groups.deleteAll()
        repos.courses.deleteAll()
        repos.rooms.deleteAll()
        repos.teachers.deleteAll()
    }
}