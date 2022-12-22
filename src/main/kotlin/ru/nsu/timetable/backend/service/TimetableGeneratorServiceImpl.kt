package ru.nsu.timetable.backend.service

import ru.nsu.timetable.backend.entity.TimetableEntry
import ru.nsu.timetable.backend.generator.EngineAdapter

class TimetableGeneratorServiceImpl(private val repos: RepoProvider) : TimetableGeneratorService(repos) {
    override fun generate(): List<TimetableEntry> {
        val ea = EngineAdapter()
        return ea.getOutGenerated(repos)
    }
}