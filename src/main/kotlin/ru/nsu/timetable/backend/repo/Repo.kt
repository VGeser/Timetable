package ru.nsu.timetable.backend.repo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Service
import ru.nsu.timetable.backend.entity.*

interface SlotRepository: JpaRepository<Slot, Long>
fun SlotRepository.slotSet(ids: List<Long>): Set<Slot> = findAllById(ids).toSet()


interface CourseRepository: JpaRepository<Course, Long>
interface GroupRepository: JpaRepository<Group, Long>
interface RoomRepository : JpaRepository<Room, Long>
interface TeacherRepository: JpaRepository<Teacher, Long>
interface TimeTableRepository: JpaRepository<TimetableEntry, Long>{
    fun findAllByTeacher(teacher: Teacher): List<TimetableEntry>
    @Query("select t from TimetableEntry t where ?1 member  of t.group")
    fun findAllByGroup(group: Group): List<TimetableEntry>
    fun findAllByRoom(room: Room): List<TimetableEntry>
}


interface UserRepository : JpaRepository<User, Long>{
    fun findByUsername(name: String): User?
    fun existsByUsername(name: String): Boolean
}

@Service
class RepoProvider(
    val slots: SlotRepository,
    val teachers: TeacherRepository,
    val courses: CourseRepository,
    val rooms: RoomRepository,
    val groups: GroupRepository,
    val table: TimeTableRepository,
)