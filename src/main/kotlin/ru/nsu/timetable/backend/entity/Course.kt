package ru.nsu.timetable.backend.entity

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import ru.nsu.timetable.backend.config.EntitiesToIdsSerializer
import ru.nsu.timetable.backend.config.EntityToIdSerializer
import jakarta.persistence.*

@Entity
@Table(name = "courses")
class Course(
    id: Long = 0,

    @Column(name = "name")
    var name: String = "",

    //TODO: enum set
    @Column(name = "tools")
    var tools: Boolean = false,

    @Column(name = "frequency")
    var frequency: Byte = 1,

    @ManyToOne
    @JoinColumn(name="teacher")
    @JsonSerialize(using = EntityToIdSerializer::class)
    var teacher: Teacher = Teacher(),
): IdEntity(id){
    @ManyToMany(mappedBy = "courses")
    @JsonSerialize(using = EntitiesToIdsSerializer::class)
    var groups: Set<Group> = setOf()
}