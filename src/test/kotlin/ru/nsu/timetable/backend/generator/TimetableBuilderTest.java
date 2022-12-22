package ru.nsu.timetable.backend.generator;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import static org.junit.jupiter.api.Assertions.assertFalse;

class TimetableBuilderTest {

    /**
     * Teacher: Tue 1-7, Wed 1-7, Thur 1-7, courses: Course1
     * Course1: requiresTools - false, frequency: 1, groups: G1, G2.
     * G1 n: 15, по времени Tue 1-7, Wed 2-6, Thur 1-6, Fri 2-5, Sat 4
     * G2 n: 15, по времени Mon 1-6, Tue 1-7, Thur 2-6, Sat 3-4
     * Все комнаты всегда свободны и имеют оборудование
     * Rooms: К1 - 14, К2 - 120, К3 - 50, К4 - 32.
     * В результате Course1 ⇔ Tue 1 ⇔ K2-K4.
     **/
    @Test
    public void bestTestCase1() {
        List<CoursesMember> teachers = new Vector<>();
        CoursesMember teacher = new CoursesMember("Gatilov", 777, new int[]{101});
        for (int i = 0; i < 7; i++) {
            teacher.setUnitarySlotValue(Temporal.Day.Tuesday, (byte) i, true);
            teacher.setUnitarySlotValue(Temporal.Day.Wednesday, (byte) i, true);
            teacher.setUnitarySlotValue(Temporal.Day.Thursday, (byte) i, true);
        }
        teachers.add(teacher);

        List<CourseGen> courses = new Vector<>();
        CourseGen course = new CourseGen("OOP", 101, new int[]{10, 20});
        course.setTeacherID(777);
        courses.add(course);

        GroupGen group1 = new GroupGen("20213", 10, new int[]{101}, 15);
        for (int i = 0; i < 7; i++) {
            group1.setUnitarySlotValue(Temporal.Day.Tuesday, (byte) i, true);
        }
        for (int i = 1; i < 6; i++) {
            group1.setUnitarySlotValue(Temporal.Day.Wednesday, (byte) i, true);
        }
        for (int i = 0; i < 6; i++) {
            group1.setUnitarySlotValue(Temporal.Day.Thursday, (byte) i, true);
        }
        for (int i = 1; i < 5; i++) {
            group1.setUnitarySlotValue(Temporal.Day.Friday, (byte) i, true);
        }
        group1.setUnitarySlotValue(Temporal.Day.Saturday, (byte) 3, true);

        GroupGen group2 = new GroupGen("19870", 20, new int[]{101}, 15);
        for (int i = 0; i < 6; i++) {
            group2.setUnitarySlotValue(Temporal.Day.Monday, (byte) i, true);
        }
        for (int i = 0; i < 7; i++) {
            group2.setUnitarySlotValue(Temporal.Day.Tuesday, (byte) i, true);
        }
        for (int i = 1; i < 6; i++) {
            group2.setUnitarySlotValue(Temporal.Day.Thursday, (byte) i, true);
        }
        group2.setUnitarySlotValue(Temporal.Day.Saturday, (byte) 2, true);
        group2.setUnitarySlotValue(Temporal.Day.Saturday, (byte) 3, true);

        List<GroupGen> groups = new Vector<>();
        groups.add(group1);
        groups.add(group2);

        List<RoomGen> rooms = new Vector<>();
        rooms.add(new RoomGen("3120", 1, 14, true));
        rooms.add(new RoomGen("3107", 2, 120, true));
        rooms.add(new RoomGen("2266", 3, 50, true));
        rooms.add(new RoomGen("5214", 4, 32, true));
        for (RoomGen r : rooms) {
            for (int i = 0; i < 7; i++) {
                r.setUnitarySlotValue(Temporal.Day.Monday, (byte) i, true);
                r.setUnitarySlotValue(Temporal.Day.Tuesday, (byte) i, true);
                r.setUnitarySlotValue(Temporal.Day.Wednesday, (byte) i, true);
                r.setUnitarySlotValue(Temporal.Day.Thursday, (byte) i, true);
                r.setUnitarySlotValue(Temporal.Day.Friday, (byte) i, true);
                r.setUnitarySlotValue(Temporal.Day.Saturday, (byte) i, true);
                r.setUnitarySlotValue(Temporal.Day.Sunday, (byte) i, true);
            }
        }

        TimetableBuilder tb = new TimetableBuilder(teachers, groups, courses, rooms);
        TimetableBuilder.Slot[][] res = tb.generate();
        TimetableBuilder.Slot[][] empty = new TimetableBuilder.Slot[7][7];
        assertFalse(Arrays.deepEquals(res, empty));
    }


    /**
     * Teacher: Mon 1-2, Wed 3-5, Sat 2-4, courses: Course1, Course2
     * Course1: requiresTools - false, frequency: 2, groups: G1, G2
     * Course2: requiresTools - true, frequency: 1, groups: G1
     * G1 n: 22, по времени Tue 1-7, Wed 2-6, Thur 1-6, Fri 2-5, Sat 1-4
     * G2 n: 15, по времени Mon 1-6, Wed 1-7, Thur 2-6, Sat 3-4
     * Все комнаты всегда свободны
     * Rooms: К1 - 14 true, К2 - 120 false, К3 - 60 true, К4 - 50 true, К5 - 55 true, К6 - 32 true.
     */
    @Test
    public void bestTestCase2() {
        CoursesMember teacher = new CoursesMember("Gatilov", 777, new int[]{101, 552});
        teacher.setUnitarySlotValue(Temporal.Day.Monday, (byte) 0, true);
        teacher.setUnitarySlotValue(Temporal.Day.Monday, (byte) 1, true);
        for (int i = 2; i < 5; i++) {
            teacher.setUnitarySlotValue(Temporal.Day.Wednesday, (byte) i, true);
        }
        for (int i = 1; i < 4; i++) {
            teacher.setUnitarySlotValue(Temporal.Day.Saturday, (byte) i, true);
        }

        List<CoursesMember> teachers = new Vector<>();
        teachers.add(teacher);

        CourseGen course1 = new CourseGen("OOP", 101, new int[]{10, 20});
        course1.setTeacherID(777);
        course1.setTools(false);
        course1.setFrequency(2);
        CourseGen course2 = new CourseGen("sOfTwArE dEsIgN", 552, new int[]{10});
        course2.setTeacherID(777);
        course2.setTools(true);
        course2.setFrequency(1);

        List<CourseGen> courses = new Vector<>();
        courses.add(course1);
        courses.add(course2);

        GroupGen group1 = new GroupGen("20213", 10, new int[]{101, 552}, 22);
        for (int i = 0; i < 7; i++) {
            group1.setUnitarySlotValue(Temporal.Day.Tuesday, (byte) i, true);
        }
        for (int i = 1; i < 6; i++) {
            group1.setUnitarySlotValue(Temporal.Day.Wednesday, (byte) i, true);
        }
        for (int i = 0; i < 6; i++) {
            group1.setUnitarySlotValue(Temporal.Day.Thursday, (byte) i, true);
        }
        for (int i = 1; i < 5; i++) {
            group1.setUnitarySlotValue(Temporal.Day.Friday, (byte) i, true);
        }
        for (int i = 0; i < 4; i++) {
            group1.setUnitarySlotValue(Temporal.Day.Saturday, (byte) i, true);
        }

        GroupGen group2 = new GroupGen("19870", 20, new int[]{101}, 15);
        for (int i = 0; i < 6; i++) {
            group2.setUnitarySlotValue(Temporal.Day.Monday, (byte) i, true);
        }
        for (int i = 0; i < 7; i++) {
            group2.setUnitarySlotValue(Temporal.Day.Wednesday, (byte) i, true);
        }
        for (int i = 1; i < 6; i++) {
            group2.setUnitarySlotValue(Temporal.Day.Thursday, (byte) i, true);
        }
        group2.setUnitarySlotValue(Temporal.Day.Saturday, (byte) 2, true);
        group2.setUnitarySlotValue(Temporal.Day.Saturday, (byte) 3, true);

        List<GroupGen> groups = new Vector<>();
        groups.add(group1);
        groups.add(group2);

        List<RoomGen> rooms = new Vector<>();
        rooms.add(new RoomGen("3120", 1, 14, true));
        rooms.add(new RoomGen("3107", 2, 120, false));
        rooms.add(new RoomGen("2266", 3, 60, true));
        rooms.add(new RoomGen("5214", 4, 50, true));
        rooms.add(new RoomGen("2128", 5, 55, true));
        rooms.add(new RoomGen("4219", 6, 32, true));
        for (RoomGen r : rooms) {
            for (int i = 0; i < 7; i++) {
                r.setUnitarySlotValue(Temporal.Day.Monday, (byte) i, true);
                r.setUnitarySlotValue(Temporal.Day.Tuesday, (byte) i, true);
                r.setUnitarySlotValue(Temporal.Day.Wednesday, (byte) i, true);
                r.setUnitarySlotValue(Temporal.Day.Thursday, (byte) i, true);
                r.setUnitarySlotValue(Temporal.Day.Friday, (byte) i, true);
                r.setUnitarySlotValue(Temporal.Day.Saturday, (byte) i, true);
                r.setUnitarySlotValue(Temporal.Day.Sunday, (byte) i, true);
            }
        }
        TimetableBuilder tb = new TimetableBuilder(teachers, groups, courses, rooms);
        TimetableBuilder.Slot[][] res = tb.generate();
        TimetableBuilder.Slot[][] empty = new TimetableBuilder.Slot[7][7];
        assertFalse(Arrays.deepEquals(res, empty));
    }

}