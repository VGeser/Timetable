package ru.nsu.timetable.backend.repo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Service
import ru.nsu.timetable.backend.entity.*

interface SlotRepository: JpaRepository<Slot, Long>
// 'Cause immutable set breaks JPA for some reason
fun SlotRepository.slotSet(ids: List<Long>): MutableSet<Slot> = findAllById(ids).toMutableSet()




interface CourseRepository: JpaRepository<Course, Long>{
    @Query("select t from Course t where t.active")
    fun findAllActive(): List<Course>
    fun getByName(name: String): Course
}
interface GroupRepository: JpaRepository<Group, Long>{
    @Query("select t from Group t where t.active")
    fun findAllActive(): List<Group>
    fun getByName(name: String): Group
}
interface RoomRepository : JpaRepository<Room, Long>{
    @Query("select t from Room t where t.active")
    fun findAllActive(): List<Room>
    fun getByName(name: String): Room
}
interface TeacherRepository: JpaRepository<Teacher, Long>{
    @Query("select t from Teacher t where t.active")
    fun findAllActive(): List<Teacher>
    fun getByName(name: String): Teacher
}

interface TimeTableRepository: JpaRepository<TimetableEntry, Long>{
    fun findAllByTeacher(teacher: Teacher): List<TimetableEntry>
    @Query("select t from TimetableEntry t where ?1 member  of t.group")
    fun findAllByGroup(group: Group): List<TimetableEntry>
    fun findAllByRoom(room: Room): List<TimetableEntry>
}


interface UserRepository : JpaRepository<User, Long>{
    fun findByUsername(name: String): User?
    fun getByUsername(username: String): User
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