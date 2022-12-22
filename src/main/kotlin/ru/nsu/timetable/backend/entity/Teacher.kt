package ru.nsu.timetable.backend.entity

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import ru.nsu.timetable.backend.controller.EntitiesToIdsSerializer
import javax.persistence.*

@Entity
@Table(name = "teachers")
class Teacher(
    id: Long = 0,

    @Column(name = "name")
    var name: String = "",


    @ManyToMany
    @JsonSerialize(using = EntitiesToIdsSerializer::class)
    var availableSlots: Set<Slot> = setOf(),

    @OneToMany(mappedBy = "teacher")
    @JsonSerialize(using = EntitiesToIdsSerializer::class)
    var courses: Set<Course> = setOf(),
): IdEntity(id)