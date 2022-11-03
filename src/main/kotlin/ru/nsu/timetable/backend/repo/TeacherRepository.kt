package ru.nsu.timetable.backend.repo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.data.rest.core.annotation.RestResource
import ru.nsu.timetable.backend.entity.Teacher

@RepositoryRestResource()
interface TeacherRepository: JpaRepository<Teacher, Long> {
}