package ru.nsu.timetable.backend.service

import org.springframework.stereotype.Service
import ru.nsu.timetable.backend.entity.Slot
import ru.nsu.timetable.backend.repo.*

enum class ProblemKind {
    WRONG_FREQUENCY,
    NO_TOOLS,
    GROUP_WRONG_SLOT,
    GROUP_WRONG_COURSE,
    GROUP_SMALL_ROOM,
    ROOM_WRONG_SLOT,
    TEACHER_WRONG_SLOT,
    GROUP_MULTIPLE_SLOTS,
    TEACHER_MULTIPLE_SLOTS,
    ROOM_MULTIPLE_SLOTS,
}

data class TimetableProblem(
    val message: String,
    val kind: ProblemKind,
    val slotID: Slot?,
)


@Service
class TimetableValidatorService(
    val repo: TimeTableRepository,
    val teacherRepo: TeacherRepository,
    val courseRepo: CourseRepository,
    val groupRepo: GroupRepository,
    val roomRepo: RoomRepository
) {
    fun validateCurrent(): List<TimetableProblem> {
        val result = mutableListOf<TimetableProblem>()
        fun res(kind: ProblemKind, message: String, slot: Slot? = null) {
            result.add(TimetableProblem(message, kind, slot))
        }

        val table = repo.findAll()
//        val teachers = table.map { it.teacher }.toSet()
//        val groups = table.map { it.group }.toSet()
        val courses = table.map { it.course }.toSet()
//        val rooms = table.map { it.room }.toSet()
//        val slots = table.map { it.slot }.toSet()

        courses.forEach { course ->
            course.groups.forEach { group ->
                val cnt = table.count { it.course == course && it.group.contains(group) }
                if (cnt != course.frequency.toInt()) {
                    res(
                        ProblemKind.WRONG_FREQUENCY,
                        "Group ${group.name} has $cnt of course ${course.name} instead of ${course.frequency}"
                    )
                }
            }
        }

        table.forEach{
            if(!it.room.tools && it.course.tools){
                res(
                    ProblemKind.NO_TOOLS,
                    "Course ${it.course.name} requires tools",
                    it.slot
                )
            }
            if(!it.group.all { group -> group.availableSlots.contains(it.slot) }){
                res(
                    ProblemKind.GROUP_WRONG_SLOT,
                    "Group ${it.group.joinToString(", "){grp->grp.name} } is not available at this time",
                    it.slot
                )
            }
            if(!it.teacher.availableSlots.contains(it.slot)){
                res(
                    ProblemKind.TEACHER_WRONG_SLOT,
                    "Teacher ${it.teacher.name} is not available at this time",
                    it.slot
                )
            }
            if(!it.room.availableSlots.contains(it.slot)){
                res(
                    ProblemKind.ROOM_WRONG_SLOT,
                    "Room ${it.room.name} is not available at this time",
                    it.slot
                )
            }
            it.group.forEach {group ->
                if(!group.courses.contains(it.course)){
                    res(
                        ProblemKind.GROUP_WRONG_COURSE,
                        "Group ${group.name} does not have course ${it.course.name}",
                        it.slot
                    )
                }
            }
            if (it.group.sumOf { it.quantity.toInt() } > it.room.capacity){
                res(
                    ProblemKind.GROUP_SMALL_ROOM,
                    "Room ${it.room.name} is too small",
                    it.slot
                )
            }
        }
        table.groupBy { it.slot }.forEach{slot, entries ->
            if(entries.map { it.room }.let { it.size != it.distinct().size }){
                res(
                    ProblemKind.ROOM_MULTIPLE_SLOTS,
                    "Some rooms are used multiple times in one slot",
                    slot
                )
            }
            if(entries.map { it.teacher }.let { it.size != it.distinct().size }){
                res(
                    ProblemKind.TEACHER_MULTIPLE_SLOTS,
                    "Some teachers are used multiple times in one slot",
                    slot
                )
            }
            // TODO: groups
        }


        return result
    }
}