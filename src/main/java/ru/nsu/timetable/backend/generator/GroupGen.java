package ru.nsu.timetable.backend.generator;

public class GroupGen extends CoursesMember {

    private final int quantity;

    public GroupGen(String name, int ID, int[] courses, int quantity) {
        super(name, ID, courses);
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

}
