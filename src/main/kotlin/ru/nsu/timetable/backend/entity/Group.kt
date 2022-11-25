package ru.nsu.timetable.backend.entity

import javax.persistence.*

@Entity
@Table(name = "groups")
class Group(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(name = "name")
    var name: String = "",

    @Column(name = "quantity")
    var quantity: Byte = 0,

    @ManyToMany
    var availableSlots: Set<Slot>,

    @ManyToMany(mappedBy = "groups")
    var courses: Set<Course>
)