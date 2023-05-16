package ru.nsu.timetable.backend.entity

import jakarta.persistence.Column
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass

@MappedSuperclass
abstract class IdEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(name = "active")
    var active: Boolean = true

){
    override fun equals(other: Any?): Boolean {
        if(this === other) return true
        if(this.javaClass != other?.javaClass) return false
        other as IdEntity
        return this.id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}