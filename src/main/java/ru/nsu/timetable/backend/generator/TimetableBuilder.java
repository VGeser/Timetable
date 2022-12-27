package ru.nsu.timetable.backend.generator;

import java.util.*;
import java.util.stream.Collectors;

public class TimetableBuilder {
    private static final Temporal.SlotState[][] allFalseTable = makeNewFixedTable(Temporal.SlotState.Shut);
    private final SlotIntersect slotIntersect;
    private static SlotWrapper[][] timetable;
    private final HashMap<Integer, CoursesMember> teacherMap;
    private final HashMap<Integer, GroupGen> groupMap;
    private final HashMap<Integer, CourseGen> coursesMap;
    private final HashMap<Integer, RoomGen> roomMap;
    private Temporal.SlotState[][] possibleSlotsContainer;

    private static Temporal.SlotState[][] makeNewFixedTable(Temporal.SlotState value) {
        Temporal.SlotState[][] res = new Temporal.SlotState[7][7];
        for (int i = 0; i < 7; i++) {
            Arrays.fill(res[i], value);
        }
        return res;
    }

    TimetableBuilder(List<CoursesMember> teachers,
                     List<GroupGen> groups,
                     List<CourseGen> courses,
                     List<RoomGen> rooms) {

        teacherMap = new HashMap<>();
        groupMap = new HashMap<>();
        coursesMap = new HashMap<>();
        roomMap = new HashMap<>();

        makeHashMaps(teachers, groups, courses, rooms);
        timetable = new SlotWrapper[7][7];
        makeEmptyTimetable();

        slotIntersect = new SlotIntersect();
    }

    public SlotWrapper[][] generate() {
        Picker picker = new Picker(coursesMap);
        int steps = getSlotNumber();
        for (int i = 0; i < steps; i++) {
            int curID = picker.getNextCourse();
            CourseGen currentCourse = coursesMap.get(curID);
            int teacherID = currentCourse.getTeacherID();

            // курс хранит айдишники групп у которых он идет
            // здесь создается локальный список объектов групп по этим айдишникам
            int[] intGroups = currentCourse.getGroups();
            List<GroupGen> curGroups = new ArrayList<>();
            for (Integer groupID : intGroups) {
                curGroups.add(groupMap.get(groupID));
            }

            // время когда все группы свободны
            possibleSlotsContainer = makeNewFixedTable(Temporal.SlotState.Free);
            if (isLooselyFree(curGroups)) {
                throw new IllegalArgumentException();
            }

            // время пересечения у препода и всех групп на курсе
            if (isLooselyFree(Collections.singletonList(teacherMap.get(teacherID)))) {
                throw new IllegalArgumentException();
            }

            // инициализация списка подходящих комнат,
            // чтобы потом по нему искать свободную
            int studentNumber = getStudentNumber(curGroups);
            List<RoomGen> curRooms = allPossibleRooms(new ArrayList<>(roomMap.values()),
                    studentNumber, currentCourse.doesRequireTools());

            int curRoomId;
            // поиск времени, когда пересекаются:
            // все группы + препод + подходящая комната
            if ((curRoomId = findRoomLoosely(curRooms)) == -1) {
                throw new IllegalArgumentException();
            }

            byte[] vals;
            if (slotIntersect.setPossibleSlots(possibleSlotsContainer)) {
                vals = slotIntersect.nextPossibleSlot();
            } else {
                List<byte[]> currentProposed = allProposedSlots(curRoomId, teacherID, curGroups);
                List<SlotIntersect.SlotWithIndex> indexed = findSlotsIndexes(
                        currentProposed, curID, curRoomId, teacherID
                );
                slotIntersect.setDeletableSlots(indexed,
                        curGroups, curRooms, teacherMap.get(teacherID));
                SlotIntersect.SlotWithIndex valsInd = slotIntersect.minimalDeletableSlot();
                unpropose(valsInd.slot, valsInd.index, picker);
                steps++;
                vals = valsInd.slot;
            }

            propose(vals, new Slot(teacherID, curID, curRoomId), picker,
                    curGroups, curRoomId, teacherID);
        }
        return timetable;
    }

    private void propose(byte[] vals, Slot slot, Picker picker,
                         List<GroupGen> groups, int roomId, int teacherId) {
        timetable[vals[0]][vals[1]].slotList.add(slot);
        picker.removeCourse();
        for (GroupGen g : groups) {
            g.setUnitarySlotValue(Temporal.idToDay(vals[1]), vals[0],
                    Temporal.SlotState.Proposed);
        }
        roomMap.get(roomId).setUnitarySlotValue(Temporal.idToDay(vals[1]), vals[0],
                Temporal.SlotState.Proposed);
        teacherMap.get(teacherId).setUnitarySlotValue(Temporal.idToDay(vals[1]), vals[0],
                Temporal.SlotState.Proposed);
    }

    private int getSlotNumber() {
        int sum = 0;
        List<CourseGen> courses = new ArrayList<>(coursesMap.values());
        for (CourseGen c : courses) {
            sum += c.getFrequency();
        }
        return sum;
    }

    private int getStudentNumber(List<GroupGen> givenGroups) {
        int sum = 0;
        for (GroupGen g : givenGroups) {
            sum += g.getQuantity();
        }
        return sum;
    }

    private void makeHashMaps(List<CoursesMember> teachers,
                              List<GroupGen> groups,
                              List<CourseGen> courses,
                              List<RoomGen> rooms) {
        for (CoursesMember t : teachers) {
            teacherMap.put(t.getID(), t);
        }
        for (GroupGen g : groups) {
            groupMap.put(g.getID(), g);
        }
        for (CourseGen c : courses) {
            coursesMap.put(c.getID(), c);
        }
        for (RoomGen r : rooms) {
            roomMap.put(r.getID(), r);
        }
    }

    private void makeEmptyTimetable() {
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                timetable[i][j] = new SlotWrapper();
            }
        }
    }

    private boolean isLooselyFree(List<? extends Temporal> list) {
        for (Temporal t : list) {
            possibleSlotsContainer = slotIntersect.getLooseIntersect(possibleSlotsContainer,
                    t.getTable());
            if (Arrays.deepEquals(possibleSlotsContainer, allFalseTable)) {
                return true;
            }
        }
        return false;
    }

    private List<RoomGen> allPossibleRooms(List<RoomGen> currentRooms, int request, boolean tools) {
        return currentRooms.stream()
                .filter(r -> (r.getCapacity() >= request) && (!tools || r.hasTools()))
                .collect(Collectors.toList());
    }

    private int findRoomLoosely(List<RoomGen> rooms) {
        for (RoomGen r : rooms) {
            possibleSlotsContainer = slotIntersect.getLooseIntersect(possibleSlotsContainer,
                    r.getTable());
            if (possibleSlotsContainer != allFalseTable) {
                return r.getID();
            }
        }
        return -1;
    }

    private List<byte[]> allProposedSlots(int roomID, int teacherID, List<GroupGen> groups) {
        List<byte[]> res = new Vector<>();
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                if (isProposedForMe(new byte[]{(byte) i, (byte) j}, roomID, teacherID, groups))
                    res.add(new byte[]{(byte) i, (byte) j});
            }
        }
        return res;
    }

    private boolean isProposedForMe(byte[] vals, int roomID, int teacherID, List<GroupGen> groups) {
        for (GroupGen g : groups) {
            if (g.getUnitarySlotValue(Temporal.idToDay(vals[1]), vals[0]) == Temporal.SlotState.Proposed)
                return true;
        }
        return roomMap.get(roomID).getUnitarySlotValue(Temporal.idToDay(vals[1]), vals[0])
                == Temporal.SlotState.Proposed ||
                teacherMap.get(teacherID).getUnitarySlotValue(Temporal.idToDay(vals[1]), vals[0])
                        == Temporal.SlotState.Proposed;
    }

    private void unpropose(byte[] vals, int index, Picker picker) {
        Slot currentSlot = timetable[vals[0]][vals[1]].slotList.get(index);
        timetable[vals[0]][vals[1]].slotList.remove(index);

        roomMap.get(currentSlot.roomID).setUnitarySlotValue(Temporal.idToDay(vals[1]), vals[0],
                Temporal.SlotState.Free);
        teacherMap.get(currentSlot.teacherID).setUnitarySlotValue(Temporal.idToDay(vals[1]), vals[0],
                Temporal.SlotState.Free);
        int[] groupsIDs = coursesMap.get(currentSlot.courseID).getGroups();
        for (int groupId : groupsIDs) {
            groupMap.get(groupId).setUnitarySlotValue(Temporal.idToDay(vals[1]), vals[0],
                    Temporal.SlotState.Free);
        }

        picker.restoreCourse(coursesMap.get(currentSlot.courseID));
    }

    private List<SlotIntersect.SlotWithIndex> findSlotsIndexes(List<byte[]> plainSlots,
                                                               int courseID,
                                                               int roomID,
                                                               int teacherID) {
        List<SlotIntersect.SlotWithIndex> res = new Vector<>();
        for (byte[] plainSlot : plainSlots) {
            int index = -5;
            Slot curSlot = new Slot(teacherID, courseID, roomID);
            for (Slot slot :
                    timetable[plainSlot[0]][plainSlot[1]].slotList) {
                if (slot.equals(curSlot)) {
                    index = timetable[plainSlot[0]][plainSlot[1]].slotList.indexOf(slot);
                    break;
                } //throw new IllegalStateException();
            }
            res.add(new SlotIntersect.SlotWithIndex(plainSlot, index));
        }
        return res;
    }

    static class Slot {
        int teacherID;
        int courseID;
        int roomID;

        Slot(int teacherID, int courseID,
             int roomID) {
            this.teacherID = teacherID;
            this.courseID = courseID;
            this.roomID = roomID;
        }

        public boolean equals(Slot s) {
            return ((this.teacherID == s.teacherID) ||
                    (this.courseID == s.courseID) ||
                    (this.roomID == s.roomID)
            );
        }
    }

    static class SlotWrapper {
        List<Slot> slotList;

        SlotWrapper() {
            slotList = new ArrayList<>();
        }
    }
}