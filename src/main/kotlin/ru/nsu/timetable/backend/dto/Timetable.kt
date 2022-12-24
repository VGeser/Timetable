package ru.nsu.timetable.backend.dto


typealias TimetableDto = Array<Array<SlotDto?>>

data class SlotDto(val id: Long, val groups: List<NameId>, val room: NameId, val teacher: NameId, val course: NameId){
    data class NameId(val id: Long, val name: String)
}


