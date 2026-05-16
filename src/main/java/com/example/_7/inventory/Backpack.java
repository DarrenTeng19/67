package com.example._7.inventory;

/*
* 角色當前手上有的道具
* */

import java.util.List;

public class Backpack {
    private int rows;
    private int cols;
    private List<PlacedItem> placedItems;

    public boolean tryRotateItem(PlacedItem placedItem) {
        Rotation originalRotation = placedItem.getRotation();

        placedItem.rotateClockwise();

        if (canPlace(placedItem)) {
            return true;
        }

        placedItem.setRotation(originalRotation);
        return false;
    }

}
