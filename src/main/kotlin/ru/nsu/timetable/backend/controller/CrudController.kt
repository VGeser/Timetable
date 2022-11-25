package ru.nsu.timetable.backend.controller

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*
import ru.nsu.timetable.backend.entity.Course
import ru.nsu.timetable.backend.entity.Group
import ru.nsu.timetable.backend.entity.Room
import ru.nsu.timetable.backend.entity.Teacher
import ru.nsu.timetable.backend.exceptions.ValidationException
import ru.nsu.timetable.backend.repo.*
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties

abstract class CrudService<C, P> {
    abstract fun create(dto: C): Long
    abstract fun patch(id: Long, dto: P)
}

abstract class CrudController<EntityT, CreateDTO, PatchDTO>(
    private val repo: JpaRepository<EntityT, Long>,
    private val service: CrudService<CreateDTO, PatchDTO>,
) {
    @GetMapping("")
    fun fetchAll(): List<EntityT> {
        return repo.findAll()
    }

    @GetMapping("/{id}")
    fun fetch(@PathVariable id: Long): EntityT {
        return repo.getReferenceById(id)
    }

    @PostMapping("")
    fun post(@RequestBody dto: CreateDTO): Long {
        return service.create(dto)
    }

    @PatchMapping("/{id}")
    fun patch(@PathVariable id: Long, @RequestBody dto: PatchDTO) {
        service.patch(id, dto)
    }
}

@Suppress("UNCHECKED_CAST")
fun validateNulls(t: Any) {
    t::class.declaredMemberProperties.forEach {
        if ((it as KProperty1<Any, *>).get(t) == null) {
            throw ValidationException("${t::class.simpleName}.${it.name} is required")
        }
    }
}


data class TeacherDto(val name: String?, val slots: List<Long>?)

@RequestMapping("/api/v1/teachers")
@RestController
@SecurityRequirement(name = "token")
class TeacherController(repo: TeacherRepository, service: TeacherService) :
    CrudController<Teacher, TeacherDto, TeacherDto>(
        repo,
        service
    )

@Service
class TeacherService(
    private val repo: TeacherRepository,
    private val slotRepo: SlotRepository,
) : CrudService<TeacherDto, TeacherDto>() {
    override fun create(dto: TeacherDto): Long {
        validateNulls(dto)
        return repo.save(Teacher(name = dto.name!!, availableSlots = slotRepo.slotSet(dto.slots!!))).id
    }

    override fun patch(id: Long, dto: TeacherDto) {
        val teacher = repo.getReferenceById(id)
        dto.name?.let { teacher.name = it }
        dto.slots?.let { teacher.availableSlots = slotRepo.slotSet(it) }
        repo.save(teacher)
    }
}


data class RoomDto(val name: String?, val capacity: Int?, val tools: Boolean?, val slots: List<Long>?)

@RequestMapping("/api/v1/rooms")
@SecurityRequirement(name = "token")
@RestController
class RoomController(repo: RoomRepository, service: RoomService) :
    CrudController<Room, RoomDto, RoomDto>(repo, service)

@Service
class RoomService(
    private val repo: RoomRepository,
    private val slotRepo: SlotRepository,
) : CrudService<RoomDto, RoomDto>() {
    override fun create(dto: RoomDto): Long {
        validateNulls(dto)
        return repo.save(
            Room(
                name = dto.name!!,
                capacity = dto.capacity!!,
                tools = dto.tools!!,
                availableSlots = slotRepo.slotSet(dto.slots!!)
            )
        ).id
    }

    override fun patch(id: Long, dto: RoomDto) {
        val room = repo.getReferenceById(id)
        dto.name?.let { room.name = it }
        dto.capacity?.let { room.capacity = it }
        dto.tools?.let { room.tools = it }
        dto.slots?.let { room.availableSlots = slotRepo.slotSet(it) }
        repo.save(room)
    }
}


data class CourseDto(val name: String?, val tools: Boolean?, val frequency: Int?)

@RequestMapping("/api/v1/courses")
@SecurityRequirement(name = "token")
@RestController
class CourseController(repo: CourseRepository, service: CourseService) :
    CrudController<Course, CourseDto, CourseDto>(repo, service)

@Service
class CourseService(
    private val repo: CourseRepository,
) : CrudService<CourseDto, CourseDto>() {
    override fun create(dto: CourseDto): Long {
        validateNulls(dto)
        return repo.save(
            Course(
                name = dto.name!!,
                tools = dto.tools!!,
                frequency = dto.frequency!!.toByte(),
                groups = emptySet()
            )
        ).id
    }

    override fun patch(id: Long, dto: CourseDto) {
        val course = repo.getReferenceById(id)
        dto.name?.let { course.name = it }
        dto.tools?.let { course.tools = it }
        dto.frequency?.let { course.frequency = it.toByte() }
        repo.save(course)
    }
}


data class GroupDto(val name: String?, val quantity: Long?, val availableSlots: List<Long>?, val courses: List<Long>?)

@RequestMapping("/api/v1/groups")
@SecurityRequirement(name = "token")
@RestController
class GroupController(repo: RoomRepository, service: GroupService) :
    CrudController<Room, GroupDto, GroupDto>(repo, service)

@Service
class GroupService(
    private val repo: GroupRepository,
    private val courseRepo: CourseRepository,
    private val slotRepo: SlotRepository,
) : CrudService<GroupDto, GroupDto>() {
    override fun create(dto: GroupDto): Long {
        validateNulls(dto)
        return repo.save(
            Group(
                name = dto.name!!,
                quantity = dto.quantity!!.toByte(),
                availableSlots = slotRepo.slotSet(dto.availableSlots!!),
                courses = courseRepo.findAllById(dto.courses!!).toSet()
            )
        ).id
    }

    override fun patch(id: Long, dto: GroupDto) {
        val group = repo.getReferenceById(id)
        dto.name?.let { group.name = it }
        dto.quantity?.let { group.quantity = it.toByte() }
        dto.availableSlots?.let { group.availableSlots = slotRepo.slotSet(it) }
        dto.courses?.let { group.courses = courseRepo.findAllById(it).toSet() }
        repo.save(group)
    }
}