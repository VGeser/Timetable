package ru.nsu.timetable.backend.generator;

public class Temporal extends Named {

    private final boolean[][] table;

    public Temporal(String name, int ID) {
        super(name, ID);
        table = new boolean[7][7];
    }

    public static Temporal.Day idToDay(byte id) {
        switch (id) {
            case 0: {
                return Temporal.Day.Monday;
            }
            case 1: {
                return Temporal.Day.Tuesday;
            }
            case 2: {
                return Temporal.Day.Wednesday;
            }
            case 3: {
                return Temporal.Day.Thursday;
            }
            case 4: {
                return Temporal.Day.Friday;
            }
            case 5: {
                return Temporal.Day.Saturday;
            }
            case 6: {
                return Temporal.Day.Sunday;
            }
            default:
                throw new IllegalArgumentException();
        }
    }

    private byte defineColumn(Day day) {
        switch (day) {
            case Monday: {
                return (byte) 0;
            }
            case Tuesday: {
                return (byte) 1;
            }
            case Wednesday: {
                return (byte) 2;
            }
            case Thursday: {
                return (byte) 3;
            }
            case Friday: {
                return (byte) 4;
            }
            case Saturday: {
                return (byte) 5;
            }
            case Sunday: {
                return (byte) 6;
            }
            default:
                throw new IllegalArgumentException();
        }
    }

    public void setUnitarySlotValue(Day day, byte slot, boolean value) {
        table[slot][defineColumn(day)] = value;
    }

    public boolean getUnitarySlotValue(Day day, byte slot) {
        return table[slot - 1][defineColumn(day)];
    }

    public boolean[][] getTable() {
        return table;
    }

    public enum Day {
        Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday;
    }
}
