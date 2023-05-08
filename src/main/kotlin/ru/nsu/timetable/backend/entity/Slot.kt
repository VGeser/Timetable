package ru.nsu.timetable.backend.entity

import jakarta.persistence.*

@Entity
@Table(name = "slots")
class Slot(
    id: Long = 0,

    @Column(name = "time_start")
    var timeStart: Long = 0,

    @Column(name = "time_end")
    var timeEnd: Long = 0,

    //totally 49 slots (including Sunday)
    @Column(name = "day")
    var day: Byte = 0,

    @Column(name = "slot_row")
    var slotRow: Byte = 0
): IdEntity(id)