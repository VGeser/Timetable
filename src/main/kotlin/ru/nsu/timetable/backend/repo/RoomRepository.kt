package ru.nsu.timetable.backend.repo

import org.springframework.data.jpa.repository.JpaRepository
import ru.nsu.timetable.backend.entity.Room

interface RoomRepository : JpaRepository<Room, Long> {
}