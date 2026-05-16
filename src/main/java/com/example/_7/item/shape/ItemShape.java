package com.example._7.item.shape;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
* 某個物件的形狀資料
* 代表一個 item 實際佔了哪些格子
* */
public record ItemShape(int width, int height, List<GridOffset> occupiedCells) {
    public ItemShape {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Item shape size must be positive.");
        }

        if (occupiedCells == null || occupiedCells.isEmpty()) {
            throw new IllegalArgumentException("Occupied cells cannot be empty.");
        }

        occupiedCells = List.copyOf(occupiedCells);

        Set<GridOffset> uniqueCells = new HashSet<>();

        for (GridOffset cell : occupiedCells) {
            if (cell.row() < 0 || cell.row() >= height
            || cell.col() < 0 || cell.col() >= width) {
                throw new IllegalArgumentException("Occupied cell is out of shape bounds.");
            }

            if (!uniqueCells.add(cell)){
                throw new IllegalArgumentException("Duplicate occupied cell detected.");
            }
        }
    }
}
