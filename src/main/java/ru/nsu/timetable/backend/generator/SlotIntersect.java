package ru.nsu.timetable.backend.generator;

import java.util.Collections;
import java.util.Stack;

public class SlotIntersect {

    // об эффективности:
    // 1) 7*7=49 итераций это ничто
    // 2) выполняется только одно действие, причем элементарное
    // его легко предсказать и закинуть в конвейер
    // 3) внутри себя стримы - это те же циклы, а писать их сложнее (ну мне)

    private Stack<byte[]> possibleSlots;

    public boolean[][] getIntersect(boolean[][] slot1, boolean[][] slot2) {
        boolean[][] res = new boolean[7][7];
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                res[i][j] = slot1[i][j] & slot2[i][j];
            }
        }
        return res;
    }

    public void setPossibleSlots(boolean[][] table) {
        possibleSlots = new Stack<>();
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                if (table[i][j]) {
                    possibleSlots.add(new byte[]{(byte) i, (byte) j});
                }
            }
        }
        Collections.shuffle(possibleSlots);
    }

    public byte[] nextPossibleSlot() {
        if (!possibleSlots.isEmpty()) {
            return possibleSlots.pop();
        } else {
            return new byte[]{};
        }
    }
}
