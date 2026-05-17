package com.example._7.item.shape;

import com.example._7.inventory.Rotation;
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

    /**
     * 根據旋轉角度回傳旋轉後的形狀
     */
    public ItemShape rotated(Rotation rotation) {
        return switch(rotation) {
            case DEGREE_0 -> this;
            case DEGREE_90 -> rotateClockwise();
            case DEGREE_180 -> rotateClockwise().rotateClockwise();
            case DEGREE_270 -> rotateClockwise().rotateClockwise().rotateClockwise();
        };
    }

    /**
     * 順時針旋轉 90 度
     */
    private ItemShape rotateClockwise() {
        // 旋轉後的新寬高互換
        int newWidth = this.height;
        int newHeight = this.width;

        // 轉換佔據的格子座標
        List<GridOffset> rotatedCells = occupiedCells.stream()
                .map(cell -> new GridOffset(cell.col(), this.width - 1 - cell.row()))
                .collect(Collectors.toList());

        return new ItemShape(newWidth, newHeight, rotatedCells);
    }
}