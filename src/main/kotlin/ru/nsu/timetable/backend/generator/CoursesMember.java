package ru.nsu.timetable.backend.generator;

public class CoursesMember extends Temporal {
    // teacher would be an instance of CoursesMember
    // bc teacher has only courses other than table and name
    private final int[] courses;

    public CoursesMember(String name, int ID, int[] courses) {
        super(name, ID);
        this.courses = courses;
    }

    public int[] getCourses() {
        return courses;
    }
}
