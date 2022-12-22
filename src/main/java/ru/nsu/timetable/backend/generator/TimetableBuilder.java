package ru.nsu.timetable.backend.generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class TimetableBuilder {
    private static final boolean[][] allFalseTable = makeNewFixedTable(false);
    private static Slot[][] timetable;
    private final SlotIntersect slotIntersect;
    private final HashMap<Integer, CoursesMember> teacherMap;
    private final HashMap<Integer, GroupGen> groupMap;
    private final HashMap<Integer, CourseGen> coursesMap;
    private final HashMap<Integer, RoomGen> roomMap;

    TimetableBuilder(List<CoursesMember> teachers,
                     List<GroupGen> groups,
                     List<CourseGen> courses,
                     List<RoomGen> rooms) {

        teacherMap = new HashMap<>();
        groupMap = new HashMap<>();
        coursesMap = new HashMap<>();
        roomMap = new HashMap<>();

        makeHashMaps(teachers, groups, courses, rooms);
        timetable = new Slot[7][7];
        makeEmptyTimetable();

        slotIntersect = new SlotIntersect();
    }

    private static boolean[][] makeNewFixedTable(boolean value) {
        boolean[][] res = new boolean[7][7];
        for (int i = 0; i < 7; i++) {
            Arrays.fill(res[i], value);
        }
        return res;
    }

    //TODO: при добавлении в стек шага, у комнаты закрывать слот!!!!

    public Slot[][] generate() {
        Picker picker = new Picker(coursesMap);
        int slotNumber = getSlotNumber();
        for (int i = 0; i < slotNumber; i++) {
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
            boolean[][] allGrops = makeNewFixedTable(true);
            for (GroupGen g : curGroups) {
                allGrops = slotIntersect.getIntersect(allGrops, g.getTable());
            }

            // время пересечения у препода и всех групп на курсе
            boolean[][] teacherSlot = teacherMap.get(teacherID).getTable();
            boolean[][] teacherAndGroups = slotIntersect.getIntersect(allGrops, teacherSlot);

            // инициализация списка подходящих комнат,
            // чтобы потом по нему искать свободную
            int studentNumber = getStudentNumber(curGroups);
            picker.setCurrentRooms(new ArrayList<>(roomMap.values()),
                    studentNumber, currentCourse.doesRequireTools());
            int curRoomId = picker.getNextRoom();

            // поиск времени, когда пересекаются:
            // все группы + препод + подходящая комната
            boolean[][] roomsAndPeople = new boolean[7][7];
            while (curRoomId != -1) {
                roomsAndPeople = slotIntersect.getIntersect(teacherAndGroups,
                        roomMap.get(curRoomId).getTable());
                if (roomsAndPeople != allFalseTable) {
                    break;
                }
                curRoomId = picker.getNextRoom();
            }

            slotIntersect.setPossibleSlots(roomsAndPeople);
            byte[] vals = slotIntersect.nextPossibleSlot();

            timetable[vals[0]][vals[1]] = new Slot(teacherID, curID, curRoomId);
            picker.removeCourse();
            for (GroupGen g : curGroups) {
                g.setUnitarySlotValue(Temporal.idToDay(vals[1]), vals[0], false);
            }
            roomMap.get(curRoomId).setUnitarySlotValue(Temporal.idToDay(vals[1]), vals[0], false);
            teacherMap.get(teacherID).setUnitarySlotValue(Temporal.idToDay(vals[1]), vals[0], false);
        }
        return timetable;
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
            Arrays.fill(timetable[i],
                    new Slot(-1, -1, -1));
        }
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
    }
}
