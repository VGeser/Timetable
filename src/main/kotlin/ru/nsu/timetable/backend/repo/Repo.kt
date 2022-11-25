package ru.nsu.timetable.backend.repo

import org.springframework.data.jpa.repository.JpaRepository
import ru.nsu.timetable.backend.entity.Course
import ru.nsu.timetable.backend.entity.Group
import ru.nsu.timetable.backend.entity.Slot

interface SlotRepository: JpaRepository<Slot, Long>
fun SlotRepository.slotSet(ids: List<Long>): Set<Slot> = findAllById(ids).toSet()


interface CourseRepository: JpaRepository<Course, Long>
interface GroupRepository: JpaRepository<Group, Long>