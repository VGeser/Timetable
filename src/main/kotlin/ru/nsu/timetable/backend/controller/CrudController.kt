package ru.nsu.timetable.backend.controller



//data class GroupDto(val name: String?, val quantity: Long?, val availableSlots: List<Long>?, val courses: List<Long>?)
//
//@RequestMapping("/api/v1/groups")
//@SecurityRequirement(name = "token")
//@RestController
//class GroupController(repo: GroupRepository, service: GroupService) :
//    CrudController<Group, GroupDto, GroupDto>(repo, service)
//
//@Service
//class GroupService(
//    private val repo: GroupRepository,
//    private val courseRepo: CourseRepository,
//    private val slotRepo: SlotRepository,
//) : CrudService<GroupDto, GroupDto>() {
//    override fun create(dto: GroupDto): Long {
//        validateNulls(dto)
//        return repo.save(
//            Group(
//                name = dto.name!!,
//                quantity = dto.quantity!!.toByte(),
//                availableSlots = slotRepo.slotSet(dto.availableSlots!!),
//                courses = courseRepo.findAllById(dto.courses!!).toSet()
//            ).also {
//                println(it.courses)
//            }
//
//        ).id
//    }
//
//    override fun patch(id: Long, dto: GroupDto) {
//        val group = repo.getReferenceById(id)
//        dto.name?.let { group.name = it }
//        dto.quantity?.let { group.quantity = it.toByte() }
//        dto.availableSlots?.let { group.availableSlots = slotRepo.slotSet(it) }
//        dto.courses?.let { group.courses = courseRepo.findAllById(it).toSet() }
//        repo.save(group)
//    }
//}
//
