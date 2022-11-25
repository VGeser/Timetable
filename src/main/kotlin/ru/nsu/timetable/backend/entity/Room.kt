package ru.nsu.timetable.backend.entity

import javax.persistence.*

@Entity
@Table(name = "rooms")
class Room(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(name = "name")
    var name: String = "",

    @Column(name = "capacity")
    var capacity: Int = 0,

    @Column(name = "tools")
    var tools: Boolean = false,

    //slot is present only if its value is true
    @ManyToMany
    var availableSlots: Set<Slot>
)