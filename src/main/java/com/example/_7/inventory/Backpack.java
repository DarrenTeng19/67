package com.example._7.inventory;

import java.util.ArrayList;
import java.util.List;

public class Backpack {
    private int rows;
    private int cols;
    private List<PlacedItem> placedItems;

    public Backpack() {
        this.rows = 5;
        this.cols = 5;
        this.placedItems = new ArrayList<>();
    }

    public List<PlacedItem> getPlacedItems() {
        return placedItems;
    }

    public boolean tryRotateItem(PlacedItem placedItem) {
        Rotation originalRotation = placedItem.getRotation();

        placedItem.rotateClockwise();

        if (canPlace(placedItem)) {
            return true;
        }

        placedItem.setRotation(originalRotation);
        return false;
    }

    private boolean canPlace(PlacedItem placedItem) {
        // 暫時簡單實現：允許放置
        return true;
    }
}