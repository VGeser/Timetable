package ru.nsu.timetable.backend.generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Picker {
    private static List<smallCourse> longerList;
    private static List<RoomGen> roomList;

    public Picker(HashMap<Integer, CourseGen> courses) {
        longerList = new ArrayList<>(courses.size());
        prolong(courses);
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

    public void setCurrentRooms(List<RoomGen> currentRooms, int request, boolean tools) {
        roomList = currentRooms.stream()
                .filter(r -> (r.getCapacity() >= request) && (!tools || r.hasTools()))
                .collect(Collectors.toList());
    }

    public int getNextRoom() {
        if (!roomList.isEmpty()) {
            Collections.shuffle(roomList);
            return roomList.get(0).getID();
        } else return -1;
    }

    public void removeCourse() {
        longerList.remove(0);
    }

    static class smallCourse {
        final int originalFrequency;
        final int id;
        int currentFrequency;

        smallCourse(int originalFrequency, int id) {
            this.originalFrequency = originalFrequency;
            this.currentFrequency = originalFrequency;
            this.id = id;
        }
    }

}
