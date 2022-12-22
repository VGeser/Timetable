package ru.nsu.timetable.backend.generator

import ru.nsu.timetable.backend.entity.*
import ru.nsu.timetable.backend.service.RepoProvider

class EngineAdapter {
    fun getOutGenerated(repos: RepoProvider): List<TimetableEntry> {
        val givenSlots = repos.slots.findAll().toList()
        val givenTeachers = repos.teachers.findAll().toMutableList()
        val givenCourses = repos.courses.findAll().toMutableList()
        val givenRooms = repos.rooms.findAll().toMutableList()
        val givenGroups = repos.groups.findAll().toMutableList()

        val adaptedTeachers = adaptTeachers(givenTeachers)
        val adaptedCourses = adaptCourses(givenCourses)
        val adaptedRooms = adaptRooms(givenRooms)
        val adaptedGroups = adaptGroups(givenGroups)

        val builder = TimetableBuilder(adaptedTeachers, adaptedGroups, adaptedCourses, adaptedRooms)
        val computeRes = builder.generate()

        return adaptRes(computeRes, givenTeachers, givenCourses, givenRooms)
    }

    private fun adaptTeachers(inTeachers: MutableList<Teacher>): List<CoursesMember> {
        val res = mutableListOf<CoursesMember>()

        inTeachers.forEach {
            //TODO: как достать все курсы, которые ведет препод?
            // и почему они не хранятся внутри самого тичера?
            // реально нужен лонг на айди?
            val teacherCourses = IntArray(5)
            var currentTeacher = CoursesMember(it.name, it.id.toInt(), teacherCourses)
            currentTeacher = adaptTime(it.availableSlots, currentTeacher) as CoursesMember
            res.add(currentTeacher)
        }
        return res
    }

    private fun adaptTime(inSlots: Set<Slot>, toAlter: Temporal): Temporal {
        inSlots.forEach {
            toAlter.setUnitarySlotValue(Temporal.idToDay(it.day), it.slotRow, true)
        }
        return toAlter
    }

    private fun adaptCourses(inCourses: MutableList<Course>): List<CourseGen> {
        val res = mutableListOf<CourseGen>()

        inCourses.forEach {
            val courseGroups = IntArray(5)
            //TODO: зачем курсу иметь сет из (объектов) групп?
            // ему достаточно сета айдишников
            // или я опять не понимаю
            val currentCourse = CourseGen(it.name, it.id.toInt(), courseGroups)
            res.add(currentCourse)
        }
        return res
    }

    private fun adaptRooms(inRooms: MutableList<Room>): List<RoomGen> {
        val res = mutableListOf<RoomGen>()

        inRooms.forEach {
            var currentRoom = RoomGen(it.name, it.id.toInt(), it.capacity, it.tools)
            currentRoom = adaptTime(it.availableSlots, currentRoom) as RoomGen
            res.add(currentRoom)
        }
        return res
    }

    private fun adaptGroups(inGroups: MutableList<Group>): List<GroupGen> {
        //TODO: почему сет (объектов) курсов, а не айдишников курсов?
        // и как мне оотуда достать массив интов?
        val res = mutableListOf<GroupGen>()

        inGroups.forEach {
            val groupCourses = IntArray(5)
            //TODO: размер группы внутри генератора в байтах (в этом есть смысл)
            var currentGroup = GroupGen(it.name, it.id.toInt(), groupCourses, it.quantity.toInt())
            currentGroup = adaptTime(it.availableSlots, currentGroup) as GroupGen
            res.add(currentGroup)
        }
        return res
    }

    private fun adaptRes(
        inTable: Array<Array<TimetableBuilder.Slot>>,
        inTeachers: MutableList<Teacher>,
        inCourses: MutableList<Course>,
        inRooms: MutableList<Room>
    ): List<TimetableEntry> {
        val res = mutableListOf<TimetableEntry>()
        var nextId: Long = 0
        for (i in 0..6) {
            for (j in 0..6) {
                if (inTable[i][j].courseID == -1) {
                    continue
                } else {
                    //TODO: откуда брать время начала и конца?
                    // где-то должна быть таблица айди-время
                    var s: Slot = Slot(i * j.toLong(), 0, 0, j.toByte(), i.toByte())
                    //TODO: откуда брать группы?
                    // это все группы, указанные у курса, и на каждую надо новый энтри писать
                    /*res.add(TimetableEntry(nextId,s,
                        inTeachers.get(inTable[i][j].teacherID),

                        inCourses.get(inTable[i][j].courseID)
                        ))*/
                }

            }
        }
        return res
    }
}