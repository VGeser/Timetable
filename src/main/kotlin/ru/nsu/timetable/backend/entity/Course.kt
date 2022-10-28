package ru.nsu.timetable.backend.entity

import javax.persistence.*

@Entity
@Table(name = "courses")
class Course(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(name = "name")
    var name: String = "",

    //TODO: enum set
    @Column(name = "tools")
    var tools: Boolean = false,

    @Column(name = "frequency")
    var frequency: Byte = 1,

    @ManyToMany
    var groups: Set<Group>
)