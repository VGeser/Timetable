package ru.nsu.timetable.backend.repo

import org.springframework.data.jpa.repository.JpaRepository
import ru.nsu.timetable.backend.entity.*

interface SlotRepository: JpaRepository<Slot, Long>
fun SlotRepository.slotSet(ids: List<Long>): Set<Slot> = findAllById(ids).toSet()


interface CourseRepository: JpaRepository<Course, Long>
interface GroupRepository: JpaRepository<Group, Long>
interface RoomRepository : JpaRepository<Room, Long>
interface TeacherRepository: JpaRepository<Teacher, Long>
interface TimeTableRepository: JpaRepository<TimetableEntry, Long>{

}


interface UserRepository : JpaRepository<User, Long>{
    fun findByUsername(name: String): User?
    fun existsByUsername(name: String): Boolean
}