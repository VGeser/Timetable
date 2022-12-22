package ru.nsu.timetable.backend.generator;

public class RoomGen extends Temporal {
    private final boolean hasTools;

    private final int capacity;

    public RoomGen(String name, int ID, int capacity, boolean hasTools) {
        super(name, ID);
        this.capacity = capacity;
        this.hasTools = hasTools;
    }

    public boolean hasTools() {
        return hasTools;
    }

    public int getCapacity() {
        return capacity;
    }

}
