package com.example._7.item.shape;

import com.example._7.inventory.Rotation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    // item 旋轉實作 供背包判定旋轉是否合法
    public ItemShape rotated(Rotation rotation) {
        if (rotation == null || rotation == Rotation.DEGREE_0) {
            return this;
        }

        List<GridOffset> rotatedCells = new ArrayList<>();

        return switch (rotation) {
            case DEGREE_0 -> this;

            case DEGREE_90 -> {
                for (GridOffset cell : occupiedCells) {
                    int newRow = cell.col();
                    int newCol = height - 1 - cell.row();
                    rotatedCells.add(new GridOffset(newRow, newCol));
                }

                yield new ItemShape(
                        height,
                        width,
                        rotatedCells
                );
            }

            case DEGREE_180 -> {
                for (GridOffset cell : occupiedCells) {
                    int newRow = height - 1 - cell.row();
                    int newCol = width - 1 - cell.col();
                    rotatedCells.add(new GridOffset(newRow, newCol));
                }

                yield new ItemShape(
                        width,
                        height,
                        rotatedCells
                );
            }

            case DEGREE_270 -> {
                for (GridOffset cell : occupiedCells) {
                    int newRow = width - 1 - cell.col();
                    int newCol = cell.row();
                    rotatedCells.add(new GridOffset(newRow, newCol));
                }

                yield new ItemShape(
                        height,
                        width,
                        rotatedCells
                );
            }
        };
    }
}
