package com.example._7.inventory;

import com.example._7.item.Item;

import java.util.ArrayList;
import java.util.List;

/*
 * 倉庫：用來放 player 已經獲得，但目前未放入 Backpack 的道具。
 * MVP：無上限。
 */
public class Storage {
    private final List<Item> items;

    public Storage() {
        this.items = new ArrayList<>();
    }

    public List<Item> getItems() {
        return List.copyOf(items);
    }

    public boolean addItem(Item item) {
        if (item == null) {
            return false;
        }

        items.add(item);
        return true;
    }

    public boolean removeItem(Item item) {
        if (item == null) {
            return false;
        }

        return items.remove(item);
    }

    public boolean contains(Item item) {
        return items.contains(item);
    }
}