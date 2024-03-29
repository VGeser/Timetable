package ru.nsu.timetable.backend.entity

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import ru.nsu.timetable.backend.config.EntitiesToIdsSerializer
import jakarta.persistence.*

@Entity
@Table(name = "teachers")
class Teacher(
    id: Long = 0,

    @Column(name = "name")
    var name: String = "",


    @ManyToMany
    @JsonSerialize(using = EntitiesToIdsSerializer::class)
    var availableSlots: MutableSet<Slot> = mutableSetOf(),

    @OneToMany(mappedBy = "teacher")
    @JsonSerialize(using = EntitiesToIdsSerializer::class)
    var courses: MutableSet<Course> = mutableSetOf(),
): IdEntity(id)