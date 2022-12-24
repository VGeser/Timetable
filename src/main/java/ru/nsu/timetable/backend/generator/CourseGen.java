package ru.nsu.timetable.backend.generator;

public class CourseGen extends Named {
    private boolean requiresTools;

    private final int[] groups;
    private int frequency;
    private int teacherID;

    public CourseGen(String name, int ID, int[] groups) {
        super(name, ID);
        requiresTools = false;
        this.groups = groups;
        frequency = 1;
    }

    public void setTools(boolean value) {
        requiresTools = value;
    }

    public int getTeacherID() {
        return teacherID;
    }

    public void setTeacherID(int teacherID) {
        this.teacherID = teacherID;
    }

    public boolean doesRequireTools() {
        return requiresTools;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int[] getGroups() {
        return groups;
    }
}
