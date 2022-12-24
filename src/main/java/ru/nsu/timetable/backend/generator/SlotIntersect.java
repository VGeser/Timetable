package ru.nsu.timetable.backend.generator;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

public class SlotIntersect {

    private List<byte[]> possibleSlots;
    private List<Deletable> deletableSlots;

    //proposed == free
    public Temporal.SlotState[][] getLooseIntersect(Temporal.SlotState[][] slot1, Temporal.SlotState[][] slot2) {
        Temporal.SlotState[][] res = new Temporal.SlotState[7][7];
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                byte s1 = Temporal.toLooseByte(slot1[i][j]);
                byte s2 = Temporal.toLooseByte(slot2[i][j]);
                res[i][j] = Temporal.fromLooseByte((byte) Math.max(s1, s2));
            }
        }
        return res;
    }

    public boolean setPossibleSlots(Temporal.SlotState[][] table) {
        possibleSlots = new Vector<>();
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                if (Temporal.toStrictBoolean(table[i][j])) {
                    possibleSlots.add(new byte[]{(byte) i, (byte) j});
                }
            }
        }
        if (possibleSlots.isEmpty()) return false;
        Collections.shuffle(possibleSlots);
        return true;
    }

    public byte[] nextPossibleSlot() {
        if (!possibleSlots.isEmpty()) {
            return possibleSlots.get(0);
        } else {
            return new byte[]{};
        }
    }

    public void setDeletableSlots(List<byte[]> proposedSlots,
                                  List<GroupGen> groups,
                                  List<RoomGen> rooms,
                                  CoursesMember teacher) {
        deletableSlots = new Vector<>();
        for (byte[] slot : proposedSlots) {
            int rating = 0;
            rating += nextRating(groups, slot[1], slot[0]);
            rating += nextRating(rooms, slot[1], slot[0]);
            rating += nextRating(Collections.singletonList(teacher), slot[1], slot[0]);
            deletableSlots.add(new Deletable(slot, rating));
        }
    }

    private int nextRating(List<? extends Temporal> list, byte day, byte row) {
        int res = 0;
        for (Temporal t : list
        ) {
            if (t.getUnitarySlotValue(Temporal.idToDay(day), row)
                    == Temporal.SlotState.Proposed) {
                res++;
            }
        }
        return res;
    }

    public byte[] minimalDeletableSlot() {
        deletableSlots.sort(new sortByRank());
        return deletableSlots.get(0).slot;
    }

    static class Deletable {
        byte[] slot;
        int rank;

        Deletable(byte[] slot, int rank) {
            this.slot = slot;
            this.rank = rank;
        }
    }

    static class sortByRank implements Comparator<Deletable> {
        @Override
        public int compare(Deletable o1, Deletable o2) {
            return o1.rank - o2.rank;
        }
    }
}
