package com.example._7.inventory;

import com.example._7.item.Item;
import java.util.ArrayList;
import java.util.List;

public class Storage {
    private List<Item> items;

    public Storage() {
        this.items = new ArrayList<>();
    }

    public List<Item> getItems() {
        return items;
    }

    public void addItem(Item item) {
        items.add(item);
    }
}