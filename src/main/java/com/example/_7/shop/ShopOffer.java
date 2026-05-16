package com.example._7.shop;

import com.example._7.item.Item;

/*
* 商店商品描述與 銷售狀態
* */

public class ShopOffer {
    private final Item item;
    private boolean sold;

    public ShopOffer(Item item) {
        this.item = item;
        this.sold = false;
    }

    public Item getItem() {
        return item;
    }

    public boolean isSold() {
        return sold;
    }

    public void markAsSold() {
        this.sold = true;
    }
}
