package com.example._7.inventory;

import com.example._7.item.Item;

public class InventoryManager {

    // 先確認東西放的進 backpack 再從 storage 刪除 否則 storage 保持原狀
    public boolean moveFromStorageToBackpack(
            Storage storage,
            Backpack backpack,
            Item item,
            GridPosition position
    ) {
        if (storage == null || backpack == null || item == null || position == null) {
            return false;
        }

        if (!storage.contains(item)) {
            return false;
        }

        PlacedItem placedItem = new PlacedItem(item, position);

        if (!backpack.tryPlaceItem(placedItem)) {
            return false;
        }

        storage.removeItem(item);
        return true;
    }

    /*
    * 確認道具真的在 backpack 裡
    * 從 backpack 移除
    * 把原本的 Item 放回 storage
    * */
    public boolean moveFromBackpackToStorage(
            Backpack backpack,
            Storage storage,
            PlacedItem placedItem
    ) {
        if (backpack == null || storage == null || placedItem == null) {
            return false;
        }

        if (!backpack.removeItem(placedItem)) {
            return false;
        }

        storage.addItem(placedItem.getItem());
        return true;
    }
}