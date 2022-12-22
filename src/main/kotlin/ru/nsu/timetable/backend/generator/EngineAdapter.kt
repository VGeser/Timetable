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
        val slotPosToId = mutableMapOf<Int, MutableMap<Int, Long>>()
        givenSlots.forEach {
            if(slotPosToId[it.slotRow.toInt()] == null)slotPosToId[it.slotRow.toInt()] = mutableMapOf()
            slotPosToId[it.slotRow.toInt()]!![it.day.toInt()] = it.id
        }

        val adaptedTeachers = adaptTeachers(givenTeachers)
        val adaptedCourses = adaptCourses(givenCourses)
        val adaptedRooms = adaptRooms(givenRooms)
        val adaptedGroups = adaptGroups(givenGroups)

        val builder = TimetableBuilder(adaptedTeachers, adaptedGroups, adaptedCourses, adaptedRooms)
        val computeRes = builder.generate()

        return adaptRes(computeRes, slotPosToId)
    }

    private fun adaptTeachers(inTeachers: MutableList<Teacher>): List<CoursesMember> {
        return inTeachers.map {teacherEntity ->
            val courses = teacherEntity.courses.map { it.id.toInt() }.toIntArray()
            val teacher = CoursesMember(teacherEntity.name, teacherEntity.id.toInt(), courses)
            adaptTime(teacherEntity.availableSlots, teacher)
            teacher
        }.toList()
    }

    private fun <T: Temporal> adaptTime(inSlots: Set<Slot>, toAlter: T): T {
        inSlots.forEach {
            toAlter.setUnitarySlotValue(Temporal.idToDay(it.day), it.slotRow, true)
        }
        return toAlter
    }

    private fun adaptCourses(inCourses: MutableList<Course>): List<CourseGen> {
        return inCourses.map {entityCourse ->
            val groups = entityCourse.groups.map { it.id.toInt() }.toIntArray()
            val course = CourseGen(entityCourse.name, entityCourse.id.toInt(), groups)
            course.teacherID = entityCourse.teacher.id.toInt()
            course.frequency = entityCourse.frequency.toInt()
            course
        }.toList()
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
            val groupCourses = it.courses.map { course -> course.id.toInt() }.toIntArray()
            var currentGroup = GroupGen(it.name, it.id.toInt(), groupCourses, it.quantity.toInt())
            currentGroup = adaptTime(it.availableSlots, currentGroup) as GroupGen
            res.add(currentGroup)
        }
        return res
    }

    private fun adaptRes(
        inTable: Array<Array<TimetableBuilder.Slot>>,
        slotTable: Map<Int, Map<Int, Long>>,
    ): List<TimetableEntry> {
        return inTable.mapIndexed { row, slots ->
            slots.mapIndexed innerLoop@{ day, slot ->
                if(slot.teacherID < 0 || slot.roomID < 0 || slot.courseID < 0)return@innerLoop null
                TimetableEntry(
                    slot = Slot(id = slotTable[row]!![day]!!),
                    teacher = Teacher(id = slot.teacherID.toLong()),
                    group = setOf(), // TODO: save groups
                    course = Course(id = slot.courseID.toLong()),
                    room = Room(id = slot.roomID.toLong()),
                )
            }
        }.flatten().filterNotNull()
    }
}