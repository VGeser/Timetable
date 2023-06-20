package ru.nsu.timetable.backend.entity

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import ru.nsu.timetable.backend.config.EntitiesToIdsSerializer
import jakarta.persistence.*

@Entity
@Table(name = "rooms")
class Room(
    id: Long = 0,

    @Column(name = "name")
    var name: String = "",

    @Column(name = "capacity")
    var capacity: Int = 0,

    @Column(name = "tools")
    var tools: Boolean = false,

    //slot is present only if its value is true
    @ManyToMany
    @JsonSerialize(using = EntitiesToIdsSerializer::class)
    var availableSlots: MutableSet<Slot> = mutableSetOf(),
): IdEntity(id)