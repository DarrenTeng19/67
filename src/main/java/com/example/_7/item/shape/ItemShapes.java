package com.example._7.item.shape;

import java.util.ArrayList;
import java.util.List;

/*
 * 用來方便建立 ItemShape 的工具類別
 * utility class / factory class
 * 跟 ItemShape 不是同一個東西
 * */
public final class ItemShapes {
    private ItemShapes() {
    }

    public static ItemShape rectangle(int width, int height) {
        List<GridOffset> cells = new ArrayList<>();

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                cells.add(new GridOffset(row, col));
            }
        }

        return new ItemShape(width, height, cells);
    }

    public static ItemShape fromPattern(String... rows) {
        if (rows == null || rows.length == 0) {
            throw new IllegalArgumentException("Pattern cannot be empty.");
        }

        int height = rows.length;
        int width = rows[0].length();
        List<GridOffset> cells = new ArrayList<>();

        for (int row = 0; row < height; row++) {
            if (rows[row].length() != width) {
                throw new IllegalArgumentException("All pattern rows must have the same width.");
            }

            for (int col = 0; col < width; col++) {
                char ch = rows[row].charAt(col);
                if (ch == '1' || ch == 'X' || ch == 'x' || ch == '#') {
                    cells.add(new GridOffset(row, col));
                }
            }
        }

        return new ItemShape(width, height, cells);
    }
}