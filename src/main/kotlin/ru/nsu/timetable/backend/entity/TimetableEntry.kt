package ru.nsu.timetable.backend.entity

import javax.persistence.*

@Entity
@Table(name = "timetable")
class TimetableEntry(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "slot")
    var slot: Slot,

    @ManyToOne
    @JoinColumn(name = "teacher")
    var teacher: Teacher,

    @ManyToOne
    @JoinColumn(name = "group_")
    var group: Group,

    @ManyToOne
    @JoinColumn(name = "course")
    var course: Course,

    @ManyToOne
    @JoinColumn(name = "room")
    var room: Room
)