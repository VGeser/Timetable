package ru.nsu.timetable.backend.generator;

public class Named {
    private final String Name;
    private final int ID;

    public Named(String name, int ID) {
        this.Name = name;
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public int getID() {
        return ID;
    }

}
