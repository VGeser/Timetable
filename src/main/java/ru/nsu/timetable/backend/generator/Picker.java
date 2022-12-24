package ru.nsu.timetable.backend.generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Picker {
    private static List<smallCourse> longerList;

    public Picker(HashMap<Integer, CourseGen> courses) {
        longerList = new ArrayList<>(courses.size());
        prolong(courses);
    }

    public void restoreCourse(CourseGen course) {
        smallCourse sm = new smallCourse(course.getFrequency(), course.getID());
        longerList.add(sm);
    }

    private void prolong(HashMap<Integer, CourseGen> courses) {
        for (CourseGen c : courses.values()) {
            smallCourse sc = new smallCourse(c.getFrequency(), c.getID());
            longerList.add(sc);
            int len = c.getFrequency() - 1;
            for (int i = 0; i < len; i++) {
                longerList.add(sc);
            }
        }
    }

    public int getNextCourse() {
        if (!longerList.isEmpty()) {
            Collections.shuffle(longerList);
            return longerList.get(0).id;
        } else {
            return -1;
        }
    }

    public void removeCourse() {
        longerList.remove(0);
    }

    static class smallCourse {
        final int originalFrequency;
        final int id;

        smallCourse(int originalFrequency, int id) {
            this.originalFrequency = originalFrequency;
            this.id = id;
        }
    }

}
