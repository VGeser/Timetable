package ru.nsu.timetable.backend.generator;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import static org.junit.jupiter.api.Assertions.assertFalse;

class TimetableBuilderTest {

    /**
     * Teacher: Tue 1-7, Wed 1-7, Thur 1-7, courses: Course1
     * Course1: requiresTools - false, frequency: 1, groups: G1, G2
     * G1 n: 15, по времени Tue 1-7, Wed 2-6, Thur 1-6, Fri 2-5, Sat 4
     * G2 n: 15, по времени Mon 1-6, Tue 1-7, Thur 2-6, Sat 3-4
     * Все комнаты всегда свободны и имеют оборудование
     * Rooms: К1 - 14, К2 - 120, К3 - 50, К4 - 32.
     **/
    @Test
    public void bestTestCase1() {
        List<CoursesMember> teachers = new Vector<>();
        CoursesMember teacher = new CoursesMember("Gatilov", 777, new int[]{101});
        for (int i = 0; i < 7; i++) {
            teacher.setUnitarySlotValue(Temporal.Day.Tuesday, (byte) i, Temporal.SlotState.Free);
            teacher.setUnitarySlotValue(Temporal.Day.Wednesday, (byte) i, Temporal.SlotState.Free);
            teacher.setUnitarySlotValue(Temporal.Day.Thursday, (byte) i, Temporal.SlotState.Free);
        }
        teachers.add(teacher);

        List<CourseGen> courses = new Vector<>();
        CourseGen course = new CourseGen("OOP", 101, new int[]{10, 20});
        course.setTeacherID(777);
        courses.add(course);

        GroupGen group1 = new GroupGen("20213", 10, new int[]{101}, 15);
        for (int i = 0; i < 7; i++) {
            group1.setUnitarySlotValue(Temporal.Day.Tuesday, (byte) i, Temporal.SlotState.Free);
        }
        for (int i = 1; i < 6; i++) {
            group1.setUnitarySlotValue(Temporal.Day.Wednesday, (byte) i, Temporal.SlotState.Free);
        }
        for (int i = 0; i < 6; i++) {
            group1.setUnitarySlotValue(Temporal.Day.Thursday, (byte) i, Temporal.SlotState.Free);
        }
        for (int i = 1; i < 5; i++) {
            group1.setUnitarySlotValue(Temporal.Day.Friday, (byte) i, Temporal.SlotState.Free);
        }
        group1.setUnitarySlotValue(Temporal.Day.Saturday, (byte) 3, Temporal.SlotState.Free);

        GroupGen group2 = new GroupGen("19870", 20, new int[]{101}, 15);
        for (int i = 0; i < 6; i++) {
            group2.setUnitarySlotValue(Temporal.Day.Monday, (byte) i, Temporal.SlotState.Free);
        }
        for (int i = 0; i < 7; i++) {
            group2.setUnitarySlotValue(Temporal.Day.Tuesday, (byte) i, Temporal.SlotState.Free);
        }
        for (int i = 1; i < 6; i++) {
            group2.setUnitarySlotValue(Temporal.Day.Thursday, (byte) i, Temporal.SlotState.Free);
        }
        group2.setUnitarySlotValue(Temporal.Day.Saturday, (byte) 2, Temporal.SlotState.Free);
        group2.setUnitarySlotValue(Temporal.Day.Saturday, (byte) 3, Temporal.SlotState.Free);

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
                r.setUnitarySlotValue(Temporal.Day.Monday, (byte) i, Temporal.SlotState.Free);
                r.setUnitarySlotValue(Temporal.Day.Tuesday, (byte) i, Temporal.SlotState.Free);
                r.setUnitarySlotValue(Temporal.Day.Wednesday, (byte) i, Temporal.SlotState.Free);
                r.setUnitarySlotValue(Temporal.Day.Thursday, (byte) i, Temporal.SlotState.Free);
                r.setUnitarySlotValue(Temporal.Day.Friday, (byte) i, Temporal.SlotState.Free);
                r.setUnitarySlotValue(Temporal.Day.Saturday, (byte) i, Temporal.SlotState.Free);
                r.setUnitarySlotValue(Temporal.Day.Sunday, (byte) i, Temporal.SlotState.Free);
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
     * Rooms: К1 - 14 true, К2 - 120 false, К3 - 60 true, К4 - 50 true, К5 - 55 true, К6 - 32 true
     **/
    @Test
    public void bestTestCase2() {
        CoursesMember teacher = new CoursesMember("Gatilov", 777, new int[]{101, 552});
        teacher.setUnitarySlotValue(Temporal.Day.Monday, (byte) 0, Temporal.SlotState.Free);
        teacher.setUnitarySlotValue(Temporal.Day.Monday, (byte) 1, Temporal.SlotState.Free);
        for (int i = 2; i < 5; i++) {
            teacher.setUnitarySlotValue(Temporal.Day.Wednesday, (byte) i, Temporal.SlotState.Free);
        }
        for (int i = 1; i < 4; i++) {
            teacher.setUnitarySlotValue(Temporal.Day.Saturday, (byte) i, Temporal.SlotState.Free);
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
            group1.setUnitarySlotValue(Temporal.Day.Tuesday, (byte) i, Temporal.SlotState.Free);
        }
        for (int i = 1; i < 6; i++) {
            group1.setUnitarySlotValue(Temporal.Day.Wednesday, (byte) i, Temporal.SlotState.Free);
        }
        for (int i = 0; i < 6; i++) {
            group1.setUnitarySlotValue(Temporal.Day.Thursday, (byte) i, Temporal.SlotState.Free);
        }
        for (int i = 1; i < 5; i++) {
            group1.setUnitarySlotValue(Temporal.Day.Friday, (byte) i, Temporal.SlotState.Free);
        }
        for (int i = 0; i < 4; i++) {
            group1.setUnitarySlotValue(Temporal.Day.Saturday, (byte) i, Temporal.SlotState.Free);
        }

        GroupGen group2 = new GroupGen("19870", 20, new int[]{101}, 15);
        for (int i = 0; i < 6; i++) {
            group2.setUnitarySlotValue(Temporal.Day.Monday, (byte) i, Temporal.SlotState.Free);
        }
        for (int i = 0; i < 7; i++) {
            group2.setUnitarySlotValue(Temporal.Day.Wednesday, (byte) i, Temporal.SlotState.Free);
        }
        for (int i = 1; i < 6; i++) {
            group2.setUnitarySlotValue(Temporal.Day.Thursday, (byte) i, Temporal.SlotState.Free);
        }
        group2.setUnitarySlotValue(Temporal.Day.Saturday, (byte) 2, Temporal.SlotState.Free);
        group2.setUnitarySlotValue(Temporal.Day.Saturday, (byte) 3, Temporal.SlotState.Free);

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
                r.setUnitarySlotValue(Temporal.Day.Monday, (byte) i, Temporal.SlotState.Free);
                r.setUnitarySlotValue(Temporal.Day.Tuesday, (byte) i, Temporal.SlotState.Free);
                r.setUnitarySlotValue(Temporal.Day.Wednesday, (byte) i, Temporal.SlotState.Free);
                r.setUnitarySlotValue(Temporal.Day.Thursday, (byte) i, Temporal.SlotState.Free);
                r.setUnitarySlotValue(Temporal.Day.Friday, (byte) i, Temporal.SlotState.Free);
                r.setUnitarySlotValue(Temporal.Day.Saturday, (byte) i, Temporal.SlotState.Free);
                r.setUnitarySlotValue(Temporal.Day.Sunday, (byte) i, Temporal.SlotState.Free);
            }
        }
        TimetableBuilder tb = new TimetableBuilder(teachers, groups, courses, rooms);
        TimetableBuilder.Slot[][] res = tb.generate();
        TimetableBuilder.Slot[][] empty = new TimetableBuilder.Slot[7][7];
        assertFalse(Arrays.deepEquals(res, empty));
    }

    /**
     * Teacher: Mon 1-2, Wed 3,5, Sat 3, courses: Course1, Course2
     * Course1: requiresTools - false, frequency: 2, groups: G1, G2
     * Course2: requiresTools - true, frequency: 1, groups: G1
     * G1 n: 22, по времени Tue 1-7, Wed 2-6, Thur 1-6, Fri 2-5, Sat 1-4
     * G2 n: 15, по времени Mon 1-6, Wed 3-5, Thur 2-6, Sat 3-4
     * Все комнаты всегда свободны
     * Rooms: К1 - 14 true, К2 - 120 false, К4 - 50 true
     **/
    @Test
    public void mediumTestCase1() {
        CoursesMember teacher = new CoursesMember("Gatilov", 777, new int[]{101, 552});
        teacher.setUnitarySlotValue(Temporal.Day.Monday, (byte) 0, Temporal.SlotState.Free);
        teacher.setUnitarySlotValue(Temporal.Day.Monday, (byte) 1, Temporal.SlotState.Free);
        teacher.setUnitarySlotValue(Temporal.Day.Wednesday, (byte) 2, Temporal.SlotState.Free);
        teacher.setUnitarySlotValue(Temporal.Day.Wednesday, (byte) 4, Temporal.SlotState.Free);
        teacher.setUnitarySlotValue(Temporal.Day.Saturday, (byte) 3, Temporal.SlotState.Free);

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
            group1.setUnitarySlotValue(Temporal.Day.Tuesday, (byte) i, Temporal.SlotState.Free);
        }
        for (int i = 1; i < 6; i++) {
            group1.setUnitarySlotValue(Temporal.Day.Wednesday, (byte) i, Temporal.SlotState.Free);
        }
        for (int i = 0; i < 6; i++) {
            group1.setUnitarySlotValue(Temporal.Day.Thursday, (byte) i, Temporal.SlotState.Free);
        }
        for (int i = 1; i < 5; i++) {
            group1.setUnitarySlotValue(Temporal.Day.Friday, (byte) i, Temporal.SlotState.Free);
        }
        for (int i = 0; i < 4; i++) {
            group1.setUnitarySlotValue(Temporal.Day.Saturday, (byte) i, Temporal.SlotState.Free);
        }

        GroupGen group2 = new GroupGen("19870", 20, new int[]{101}, 15);
        for (int i = 0; i < 6; i++) {
            group2.setUnitarySlotValue(Temporal.Day.Monday, (byte) i, Temporal.SlotState.Free);
        }
        for (int i = 0; i < 7; i++) {
            group2.setUnitarySlotValue(Temporal.Day.Wednesday, (byte) i, Temporal.SlotState.Free);
        }
        for (int i = 1; i < 6; i++) {
            group2.setUnitarySlotValue(Temporal.Day.Thursday, (byte) i, Temporal.SlotState.Free);
        }
        group2.setUnitarySlotValue(Temporal.Day.Saturday, (byte) 2, Temporal.SlotState.Free);
        group2.setUnitarySlotValue(Temporal.Day.Saturday, (byte) 3, Temporal.SlotState.Free);

        List<GroupGen> groups = new Vector<>();
        groups.add(group1);
        groups.add(group2);

        List<RoomGen> rooms = new Vector<>();
        rooms.add(new RoomGen("3120", 1, 14, true));
        rooms.add(new RoomGen("3107", 2, 120, false));
        rooms.add(new RoomGen("5214", 4, 50, true));
        for (RoomGen r : rooms) {
            for (int i = 0; i < 7; i++) {
                r.setUnitarySlotValue(Temporal.Day.Monday, (byte) i, Temporal.SlotState.Free);
                r.setUnitarySlotValue(Temporal.Day.Tuesday, (byte) i, Temporal.SlotState.Free);
                r.setUnitarySlotValue(Temporal.Day.Wednesday, (byte) i, Temporal.SlotState.Free);
                r.setUnitarySlotValue(Temporal.Day.Thursday, (byte) i, Temporal.SlotState.Free);
                r.setUnitarySlotValue(Temporal.Day.Friday, (byte) i, Temporal.SlotState.Free);
                r.setUnitarySlotValue(Temporal.Day.Saturday, (byte) i, Temporal.SlotState.Free);
                r.setUnitarySlotValue(Temporal.Day.Sunday, (byte) i, Temporal.SlotState.Free);
            }
        }
        TimetableBuilder tb = new TimetableBuilder(teachers, groups, courses, rooms);
        TimetableBuilder.Slot[][] res = tb.generate();
        TimetableBuilder.Slot[][] empty = new TimetableBuilder.Slot[7][7];
        assertFalse(Arrays.deepEquals(res, empty));
    }

    /**
     * Teacher1: C1, C2; 		Mon 4, Tue 3, Tue 5, Th 1, Fr 1, Fr 6
     * Teacher2: C3; 		    Sat 4
     * Teacher3: C4, C5, C6;	Mon 1, Mon 2,  Wed 2, Fr1, Sat 2
     * Teacher4: C7, C8;		Tue 3, Wed 3, Th 5, Fr 5, Sat 5
     * Teacher5: C9;		    Mon 1, Fr 3
     * <p>
     * C1: G1, G2, G3		freq - 3, tools - true
     * C2: G3, G4		    freq - 3, tools - true
     * C3: G3, G5 		    freq - 1, tools - false
     * C4: G1, G2, G4, G5	freq - 2, tools - false
     * C5: G5		        freq - 2, tools - true
     * C6: G6			    freq - 1, tools - true
     * C7: G4, G5		    freq - 2, tools - false
     * C8: G1, G6		    freq - 3, tools - false
     * C9: G1, G2, G3, G4	freq - 2, tools - false
     * <p>
     * G1 - 15		G2 - 20	    G3 - 23			все группы всегда свободны,
     * G4 - 36	    G5 - 87	    G6 - 78			пока не проставлены
     * <p>
     * R1: 88, tools - true		    закрыта Wed, Th
     * R2: 110, tools - false 	    закрыта Tue, Wed
     * R3: 60, tools - true 		только 1, 2 пары
     * R4: 160, tools - true		закрыта Th, Sat; только 1, 2, 3 пары
     */

    @Test
    public void mediumTestCase2() {
        CoursesMember t1 = new CoursesMember("Gatilov", 777, new int[]{101, 552});
        t1.setUnitarySlotValue(Temporal.Day.Monday, (byte) 3, Temporal.SlotState.Free);
        t1.setUnitarySlotValue(Temporal.Day.Tuesday, (byte) 2, Temporal.SlotState.Free);
        t1.setUnitarySlotValue(Temporal.Day.Tuesday, (byte) 4, Temporal.SlotState.Free);
        t1.setUnitarySlotValue(Temporal.Day.Thursday, (byte) 0, Temporal.SlotState.Free);
        t1.setUnitarySlotValue(Temporal.Day.Friday, (byte) 0, Temporal.SlotState.Free);
        t1.setUnitarySlotValue(Temporal.Day.Friday, (byte) 5, Temporal.SlotState.Free);

        CoursesMember t2 = new CoursesMember("Vlasov", 555, new int[]{998});
        t2.setUnitarySlotValue(Temporal.Day.Saturday, (byte) 3, Temporal.SlotState.Free);

        CoursesMember t3 = new CoursesMember("Miginsky", 313, new int[]{919, 513, 450});
        t3.setUnitarySlotValue(Temporal.Day.Monday, (byte) 0, Temporal.SlotState.Free);
        t3.setUnitarySlotValue(Temporal.Day.Monday, (byte) 1, Temporal.SlotState.Free);
        t3.setUnitarySlotValue(Temporal.Day.Wednesday, (byte) 1, Temporal.SlotState.Free);
        t3.setUnitarySlotValue(Temporal.Day.Friday, (byte) 0, Temporal.SlotState.Free);
        t3.setUnitarySlotValue(Temporal.Day.Saturday, (byte) 1, Temporal.SlotState.Free);

        CoursesMember t4 = new CoursesMember("Postovalov", 42, new int[]{655, 244});
        t4.setUnitarySlotValue(Temporal.Day.Tuesday, (byte) 2, Temporal.SlotState.Free);
        t4.setUnitarySlotValue(Temporal.Day.Wednesday, (byte) 2, Temporal.SlotState.Free);
        t4.setUnitarySlotValue(Temporal.Day.Thursday, (byte) 4, Temporal.SlotState.Free);
        t4.setUnitarySlotValue(Temporal.Day.Friday, (byte) 4, Temporal.SlotState.Free);
        t4.setUnitarySlotValue(Temporal.Day.Saturday, (byte) 4, Temporal.SlotState.Free);

        CoursesMember t5 = new CoursesMember("Zlygostev", 117, new int[]{807});
        t5.setUnitarySlotValue(Temporal.Day.Monday, (byte) 0, Temporal.SlotState.Free);
        t5.setUnitarySlotValue(Temporal.Day.Friday, (byte) 2, Temporal.SlotState.Free);

        List<CoursesMember> teachers = new Vector<>();
        teachers.add(t1);
        teachers.add(t2);
        teachers.add(t3);
        teachers.add(t4);
        teachers.add(t5);

        CourseGen c1 = new CourseGen("OOP", 101, new int[]{10, 20, 30});
        c1.setTeacherID(777);
        c1.setTools(true);
        c1.setFrequency(3);

        CourseGen c2 = new CourseGen("sOfTwArE dEsIgN", 552, new int[]{30, 40});
        c2.setTeacherID(777);
        c2.setTools(true);
        c2.setFrequency(3);

        CourseGen c3 = new CourseGen("Team Project", 383, new int[]{30, 50});
        c3.setTeacherID(555);
        c3.setTools(false);
        c3.setFrequency(1);

        CourseGen c4 = new CourseGen("Declarative programming", 222,
                new int[]{10, 20, 40, 50});
        c4.setTeacherID(313);
        c4.setTools(false);
        c4.setFrequency(2);

        CourseGen c5 = new CourseGen("History", 400, new int[]{50});
        c5.setTeacherID(313);
        c5.setTools(true);
        c5.setFrequency(2);

        CourseGen c6 = new CourseGen("Law", 1414, new int[]{60});
        c6.setTeacherID(313);
        c6.setTools(true);
        c6.setFrequency(1);

        CourseGen c7 = new CourseGen("Statistics", 9009, new int[]{40, 50});
        c7.setTeacherID(42);
        c7.setTools(false);
        c7.setFrequency(2);

        CourseGen c8 = new CourseGen("Machine Learning", 755, new int[]{10, 60});
        c8.setTeacherID(42);
        c8.setTools(false);
        c8.setFrequency(3);

        CourseGen c9 = new CourseGen("Data Processing", 3750,
                new int[]{10, 20, 30, 40});
        c9.setTeacherID(117);
        c9.setTools(false);
        c9.setFrequency(2);

        List<CourseGen> courses = new Vector<>();
        courses.add(c1);
        courses.add(c2);
        courses.add(c3);
        courses.add(c4);
        courses.add(c5);
        courses.add(c6);
        courses.add(c7);
        courses.add(c8);
        courses.add(c9);

        List<GroupGen> groups = new Vector<>();
        groups.add(new GroupGen("20213", 10,
                new int[]{101, 222, 755, 3750}, 15));
        groups.add(new GroupGen("20214", 20,
                new int[]{101, 222, 3750}, 20));
        groups.add(new GroupGen("20215", 30,
                new int[]{101, 552, 383, 3750}, 23));
        groups.add(new GroupGen("78520", 40,
                new int[]{552, 222, 9009, 3750}, 36));
        groups.add(new GroupGen("34522", 50,
                new int[]{383, 222, 400, 9009}, 87));
        groups.add(new GroupGen("9875", 60,
                new int[]{1414, 755}, 78));
        for (GroupGen g : groups) {
            for (int i = 0; i < 7; i++) {
                g.setUnitarySlotValue(Temporal.Day.Monday, (byte) i, Temporal.SlotState.Free);
                g.setUnitarySlotValue(Temporal.Day.Tuesday, (byte) i, Temporal.SlotState.Free);
                g.setUnitarySlotValue(Temporal.Day.Wednesday, (byte) i, Temporal.SlotState.Free);
                g.setUnitarySlotValue(Temporal.Day.Thursday, (byte) i, Temporal.SlotState.Free);
                g.setUnitarySlotValue(Temporal.Day.Friday, (byte) i, Temporal.SlotState.Free);
                g.setUnitarySlotValue(Temporal.Day.Saturday, (byte) i, Temporal.SlotState.Free);
                g.setUnitarySlotValue(Temporal.Day.Sunday, (byte) i, Temporal.SlotState.Free);
            }
        }

        RoomGen r1 = new RoomGen("2128", 1, 88, true);
        for (int i = 0; i < 7; i++) {
            r1.setUnitarySlotValue(Temporal.Day.Monday, (byte) i, Temporal.SlotState.Free);
            r1.setUnitarySlotValue(Temporal.Day.Tuesday, (byte) i, Temporal.SlotState.Free);
            r1.setUnitarySlotValue(Temporal.Day.Friday, (byte) i, Temporal.SlotState.Free);
            r1.setUnitarySlotValue(Temporal.Day.Saturday, (byte) i, Temporal.SlotState.Free);
            r1.setUnitarySlotValue(Temporal.Day.Sunday, (byte) i, Temporal.SlotState.Free);
        }

        RoomGen r2 = new RoomGen("3107", 2, 110, false);
        for (int i = 0; i < 7; i++) {
            r2.setUnitarySlotValue(Temporal.Day.Monday, (byte) i, Temporal.SlotState.Free);
            r2.setUnitarySlotValue(Temporal.Day.Thursday, (byte) i, Temporal.SlotState.Free);
            r2.setUnitarySlotValue(Temporal.Day.Friday, (byte) i, Temporal.SlotState.Free);
            r2.setUnitarySlotValue(Temporal.Day.Saturday, (byte) i, Temporal.SlotState.Free);
            r2.setUnitarySlotValue(Temporal.Day.Sunday, (byte) i, Temporal.SlotState.Free);
        }

        RoomGen r3 = new RoomGen("5214", 3, 60, true);
        for (int i = 0; i < 2; i++) {
            r3.setUnitarySlotValue(Temporal.Day.Monday, (byte) i, Temporal.SlotState.Free);
            r3.setUnitarySlotValue(Temporal.Day.Tuesday, (byte) i, Temporal.SlotState.Free);
            r3.setUnitarySlotValue(Temporal.Day.Wednesday, (byte) i, Temporal.SlotState.Free);
            r3.setUnitarySlotValue(Temporal.Day.Thursday, (byte) i, Temporal.SlotState.Free);
            r3.setUnitarySlotValue(Temporal.Day.Friday, (byte) i, Temporal.SlotState.Free);
            r3.setUnitarySlotValue(Temporal.Day.Saturday, (byte) i, Temporal.SlotState.Free);
            r3.setUnitarySlotValue(Temporal.Day.Sunday, (byte) i, Temporal.SlotState.Free);
        }

        RoomGen r4 = new RoomGen("4219", 4, 160, true);
        for (int i = 0; i < 3; i++) {
            r4.setUnitarySlotValue(Temporal.Day.Monday, (byte) i, Temporal.SlotState.Free);
            r4.setUnitarySlotValue(Temporal.Day.Tuesday, (byte) i, Temporal.SlotState.Free);
            r4.setUnitarySlotValue(Temporal.Day.Wednesday, (byte) i, Temporal.SlotState.Free);
            r4.setUnitarySlotValue(Temporal.Day.Friday, (byte) i, Temporal.SlotState.Free);
            r4.setUnitarySlotValue(Temporal.Day.Sunday, (byte) i, Temporal.SlotState.Free);
        }

        List<RoomGen> rooms = new Vector<>();
        rooms.add(r1);
        rooms.add(r2);
        rooms.add(r3);
        rooms.add(r4);

        TimetableBuilder tb = new TimetableBuilder(teachers, groups, courses, rooms);
        TimetableBuilder.Slot[][] res = tb.generate();
        TimetableBuilder.Slot[][] empty = new TimetableBuilder.Slot[7][7];
        assertFalse(Arrays.deepEquals(res, empty));
    }

    @Test
    public void mediumTestCase3() {
        CoursesMember t1 = new CoursesMember("Vlasov", 555, new int[]{200, 31});
        for (int i = 0; i < 2; i++) {
            t1.setUnitarySlotValue(Temporal.Day.Tuesday, (byte) i, Temporal.SlotState.Free);
            t1.setUnitarySlotValue(Temporal.Day.Wednesday, (byte) i, Temporal.SlotState.Free);
            t1.setUnitarySlotValue(Temporal.Day.Thursday, (byte) i, Temporal.SlotState.Free);
        }
        t1.setUnitarySlotValue(Temporal.Day.Thursday, (byte) 2, Temporal.SlotState.Free);

        CoursesMember t2 = new CoursesMember("Irtegov", 777, new int[]{101});
        for (int i = 0; i < 3; i++) {
            t2.setUnitarySlotValue(Temporal.Day.Tuesday, (byte) i, Temporal.SlotState.Free);
            t2.setUnitarySlotValue(Temporal.Day.Wednesday, (byte) i, Temporal.SlotState.Free);
            t2.setUnitarySlotValue(Temporal.Day.Thursday, (byte) i, Temporal.SlotState.Free);
        }

        List<CoursesMember> teachers = new Vector<>();
        teachers.add(t1);
        teachers.add(t2);

        CourseGen c1 = new CourseGen("OS", 101, new int[]{10, 20});
        c1.setTeacherID(777);
        c1.setTools(false);
        c1.setFrequency(2);

        CourseGen c2 = new CourseGen("Jaba", 200, new int[]{10, 20});
        c2.setTeacherID(555);
        c2.setTools(true);
        c2.setFrequency(1);

        CourseGen c3 = new CourseGen("PAC", 31, new int[]{10});
        c3.setTeacherID(555);
        c3.setTools(true);
        c3.setFrequency(2);

        List<CourseGen> courses = new Vector<>();
        courses.add(c1);
        courses.add(c2);
        courses.add(c3);

        List<GroupGen> groups = new Vector<>();
        groups.add(new GroupGen("20213", 10,
                new int[]{101, 200, 31}, 13));
        groups.add(new GroupGen("20215", 20,
                new int[]{101, 200}, 50));
        for (GroupGen g : groups) {
            for (int i = 0; i < 3; i++) {
                g.setUnitarySlotValue(Temporal.Day.Tuesday, (byte) i, Temporal.SlotState.Free);
                g.setUnitarySlotValue(Temporal.Day.Wednesday, (byte) i, Temporal.SlotState.Free);
                g.setUnitarySlotValue(Temporal.Day.Thursday, (byte) i, Temporal.SlotState.Free);
            }
        }

        RoomGen r1 = new RoomGen("1154", 1, 13, true);
        r1.setUnitarySlotValue(Temporal.Day.Tuesday, (byte) 0, Temporal.SlotState.Free);
        r1.setUnitarySlotValue(Temporal.Day.Wednesday, (byte) 1, Temporal.SlotState.Free);
        r1.setUnitarySlotValue(Temporal.Day.Thursday, (byte) 2, Temporal.SlotState.Free);

        RoomGen r2 = new RoomGen("2128", 2, 100, true);
        r2.setUnitarySlotValue(Temporal.Day.Tuesday, (byte) 0, Temporal.SlotState.Free);
        r2.setUnitarySlotValue(Temporal.Day.Wednesday, (byte) 0, Temporal.SlotState.Free);
        r2.setUnitarySlotValue(Temporal.Day.Thursday, (byte) 0, Temporal.SlotState.Free);

        List<RoomGen> rooms = new Vector<>();
        rooms.add(r1);
        rooms.add(r2);

        TimetableBuilder tb = new TimetableBuilder(teachers, groups, courses, rooms);
        TimetableBuilder.Slot[][] res = tb.generate();
        TimetableBuilder.Slot[][] empty = new TimetableBuilder.Slot[7][7];
        assertFalse(Arrays.deepEquals(res, empty));
    }
}