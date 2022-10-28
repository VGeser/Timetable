package ru.nsu.timetable.backend.entity

import javax.persistence.*

@Entity
@Table(name = "groups")
class Group(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(name = "name")
    var name: Int = 0,

    @Column(name = "quantity")
    var quantity: Byte = 0,

    @ManyToMany
    var availableSlots: Set<Slot>,
    //slot is present only if its value is true

    @ManyToMany(mappedBy = "groups")
    var courses: Set<Course>
)