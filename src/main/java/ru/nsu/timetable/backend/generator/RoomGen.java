package ru.nsu.timetable.backend.generator;

public class RoomGen extends Temporal {
    private final boolean hasTools;

    private final int capacity;

    public boolean hasTools() {
        return hasTools;
    }

    public RoomGen(String name, int ID, int capacity, boolean hasTools) {
        super(name, ID);
        this.capacity = capacity;
        this.hasTools = hasTools;
    }

    public int getCapacity() {
        return capacity;
    }

}
