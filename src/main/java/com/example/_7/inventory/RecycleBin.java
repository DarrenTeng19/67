package com.example._7.inventory;

/*
* 將倉庫中道具賣掉的地方
* */

import com.example._7.item.Item;

public class RecycleBin {
    public int recycle(Item item) {
        return item.getPrice() / 2; //
    }
}
