package ru.nsu.timetable.backend.service

import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import ru.nsu.timetable.backend.entity.Slot
import ru.nsu.timetable.backend.repo.SlotRepository


@Service
class SlotService(
    val repo: SlotRepository,
) {

    @EventListener(ApplicationReadyEvent::class)
    fun generateSLots(){
        if(repo.count() != 0L){
            return
        }
        for(day in 0 until 7){
            var time: Long = 9*60 + 0
            for(slotNum in 0 until SLOTS_PER_DAY){
                repo.save(Slot(
                    timeStart = time,
                    timeEnd = time + SLOT_DURATION,
                    day = day.toByte(),
                    slotRow = slotNum.toByte(),
                ))
                time += SLOT_DURATION + SLOT_BREAK
            }
        }
    }

    fun getSlots() = repo.findAll()

    companion object {
        const val SLOTS_PER_DAY = 7
        const val SLOT_DURATION = 1*60 + 35
        const val SLOT_BREAK = 15
    }
}