package com.example._7.inventory;

import com.example._7.item.Item;
import com.example._7.item.shape.GridOffset;
import com.example._7.item.shape.ItemShape;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class Backpack {
    private static final int DEFAULT_ROWS = 5;
    private static final int DEFAULT_COLS = 5;

    private final int rows;
    private final int cols;
    private final List<PlacedItem> placedItems;

    public Backpack() {
        this(DEFAULT_ROWS, DEFAULT_COLS);
    }

    public Backpack(int rows, int cols) {
        if (rows <= 0 || cols <= 0) {
            throw new IllegalArgumentException("Backpack size must be positive.");
        }
        this.rows = rows;
        this.cols = cols;
        this.placedItems = new ArrayList<>();
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public List<PlacedItem> getPlacedItems() {
        return List.copyOf(placedItems);
    }

    public boolean tryPlaceItem(PlacedItem placedItem) {
        if (placedItem == null) {
            return false;
        }
        if (!canPlace(placedItem)) {
            return false;
        }
        placedItems.add(placedItem);
        return true;
    }

    public boolean canPlace(PlacedItem candidate) {
        return canPlace(candidate, null);
    }

    private boolean canPlace(PlacedItem candidate, PlacedItem ignoredItem) {
        if (candidate == null) {
            return false;
        }

        Set<GridPosition> candidateCells = getOccupiedBackpackCells(candidate);

        for (GridPosition cell : candidateCells) {
            if (!isInsideBackpack(cell)) {
                return false;
            }
        }

        for (PlacedItem existingItem : placedItems) {
            if (existingItem == ignoredItem) {
                continue;
            }
            Set<GridPosition> existingCells = getOccupiedBackpackCells(existingItem);
            for (GridPosition cell : candidateCells) {
                if (existingCells.contains(cell)) {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean tryRotateItem(PlacedItem placedItem) {
        if (placedItem == null || !placedItems.contains(placedItem)) {
            return false;
        }

        Rotation originalRotation = placedItem.getRotation();
        placedItem.rotateClockwise();

        if (canPlace(placedItem, placedItem)) {
            return true;
        }

        placedItem.setRotation(originalRotation);
        return false;
    }

    private Set<GridPosition> getOccupiedBackpackCells(PlacedItem placedItem) {
        Set<GridPosition> occupiedPositions = new HashSet<>();

        GridPosition basePosition = placedItem.getPosition();
        ItemShape currentShape = placedItem.getCurrentShape();

        for (GridOffset offset : currentShape.occupiedCells()) {
            int actualRow = basePosition.row() + offset.row();
            int actualCol = basePosition.col() + offset.col();
            occupiedPositions.add(new GridPosition(actualRow, actualCol));
        }

        return occupiedPositions;
    }

    private boolean isInsideBackpack(GridPosition position) {
        return position.row() >= 0
                && position.row() < rows
                && position.col() >= 0
                && position.col() < cols;
    }

    public boolean tryMoveItem(PlacedItem placedItem, GridPosition newPosition) {
        if (placedItem == null || newPosition == null) {
            return false;
        }
        if (!placedItems.contains(placedItem)) {
            return false;
        }

        GridPosition originalPosition = placedItem.getPosition();
        placedItem.setPosition(newPosition);

        if (canPlace(placedItem, placedItem)) {
            return true;
        }

        placedItem.setPosition(originalPosition);
        return false;
    }

    public boolean removeItem(PlacedItem placedItem) {
        if (placedItem == null) {
            return false;
        }
        return placedItems.remove(placedItem);
    }

    public Optional<PlacedItem> getPlacedItemAt(GridPosition position) {
        if (position == null) {
            return Optional.empty();
        }
        for (PlacedItem placedItem : placedItems) {
            if (getOccupiedBackpackCells(placedItem).contains(position)) {
                return Optional.of(placedItem);
            }
        }
        return Optional.empty();
    }

    public Optional<GridPosition> findFirstAvailablePosition(Item item) {
        if (item == null) {
            return Optional.empty();
        }
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                GridPosition position = new GridPosition(row, col);
                PlacedItem candidate = new PlacedItem(item, position);
                if (canPlace(candidate)) {
                    return Optional.of(position);
                }
            }
        }
        return Optional.empty();
    }

    public List<Item> getBattleItems() {
        List<Item> battleItems = new ArrayList<>();
        for (PlacedItem placedItem : placedItems) {
            battleItems.add(placedItem.getItem());
        }
        return List.copyOf(battleItems);
    }
}
