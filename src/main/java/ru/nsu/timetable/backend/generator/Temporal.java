package ru.nsu.timetable.backend.generator;

import java.util.Arrays;

public class Temporal extends Named {

    private final SlotState[][] table;

    public Temporal(String name, int ID) {
        super(name, ID);
        table = new SlotState[7][7];
        for (int i = 0; i < 7; i++) {
            Arrays.fill(table[i], Temporal.SlotState.Shut);
        }
    }

    public static boolean toStrictBoolean(SlotState ss) {
        return ss == SlotState.Free;
    }

    public static boolean toLooseBoolean(SlotState ss) {
        return ss == SlotState.Free || ss == SlotState.Proposed;
    }

    public static byte toLooseByte(SlotState ss) {
        switch (ss) {
            case Shut:
                return 3;
            case Proposed:
                return 2;
            case Free:
                return 1;
            default:
                return 0;
        }
    }

    public static SlotState fromLooseByte(byte b) {
        switch (b) {
            case 1:
                return SlotState.Free;
            case 2:
                return SlotState.Proposed;
            default:
                return SlotState.Shut;
        }
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

    public void setUnitarySlotValue(Day day, byte slot, SlotState value) {
        table[slot][defineColumn(day)] = value;
    }

    public SlotState getUnitarySlotValue(Day day, byte slot) {
        return table[slot][defineColumn(day)];
    }

    public SlotState[][] getTable() {
        return table;
    }

    public enum Day {
        Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday;
    }

    public enum SlotState {
        Free, Shut, Proposed;
    }
}
