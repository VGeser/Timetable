package ru.nsu.timetable.backend.entity

import javax.persistence.*

@Entity
@Table(name = "teachers")
class Teacher(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(name = "name")
    var name: String = "",

    //slot is present only if its value is true
    @ManyToMany
    var availableSlots: Set<Slot>,

    @ManyToMany
    var courses: Set<Integer>
)