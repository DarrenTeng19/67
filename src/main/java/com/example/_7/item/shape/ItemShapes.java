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

    /**
     * 建立矩形形狀
     * @param width 寬度
     * @param height 高度
     * @return 充滿整個矩形的 ItemShape
     */
    public static ItemShape rectangle(int width, int height) {
        List<GridOffset> occupiedCells = new ArrayList<>();
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                occupiedCells.add(new GridOffset(row, col));
            }
        }
        return new ItemShape(width, height, occupiedCells);
    }

    /**
     * 根據圖案建立 ItemShape
     * 例如: ItemShapes.fromPattern("##", "# ")
     * 代表 2x2 的形狀，左上、左中、右上有格子
     * @param rows 行列圖案，'#' 表示有佔據該格
     * @return ItemShape
     */
    public static ItemShape fromPattern(String... rows) {
        if (rows.length == 0) {
            throw new IllegalArgumentException("Pattern cannot be empty");
        }

        int height = rows.length;
        int width = rows[0].length();

        // 驗證所有行寬度相同
        for (String row : rows) {
            if (row.length() != width) {
                throw new IllegalArgumentException("All rows must have the same width");
            }
        }

        List<GridOffset> occupiedCells = new ArrayList<>();
        for (int row = 0; row < height; row++) {
            String currentRow = rows[row];
            for (int col = 0; col < width; col++) {
                if (currentRow.charAt(col) == '#') {
                    occupiedCells.add(new GridOffset(row, col));
                }
            }
        }

        if (occupiedCells.isEmpty()) {
            throw new IllegalArgumentException("Pattern must contain at least one '#'");
        }

        return new ItemShape(width, height, occupiedCells);
    }
}