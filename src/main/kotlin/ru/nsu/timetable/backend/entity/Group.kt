package ru.nsu.timetable.backend.entity

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import ru.nsu.timetable.backend.config.EntitiesToIdsSerializer
import jakarta.persistence.*

@Entity
@Table(name = "groups")
class Group(
    id: Long = 0,

    @Column(name = "name")
    var name: String = "",

    @Column(name = "quantity")
    var quantity: Byte = 0,

    @ManyToMany
    @JsonSerialize(using = EntitiesToIdsSerializer::class)
    var availableSlots: Set<Slot> = setOf(),

    @ManyToMany
    @JsonSerialize(using = EntitiesToIdsSerializer::class)
    var courses: Set<Course> = setOf(),
): IdEntity(id)