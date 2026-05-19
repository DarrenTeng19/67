package com.example._7.shop;

/*
 * 關卡間商店內容物的生成
 */

import com.example._7.character.CharacterClass;
import com.example._7.item.Item;
import com.example._7.item.ItemCatalog;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ShopGenerator {

    private static final int SHOP_OFFER_COUNT = 5;
    private final ItemCatalog itemCatalog;
    private final Random random;

    public ShopGenerator(ItemCatalog itemCatalog) {
        this.itemCatalog = itemCatalog;
        this.random = new Random();
    }

    public Shop generateShop(CharacterClass characterClass, int round) {
        List<Item> itemPool = getAvailableItems(characterClass, round);

        if (itemPool.isEmpty()) {
            throw new IllegalStateException("商店商品池不能為空");
        }

        List<ShopOffer> offers = new ArrayList<>();

        for (int i = 0; i < SHOP_OFFER_COUNT; i++) {
            Item randomItem = itemPool.get(random.nextInt(itemPool.size()));
            offers.add(new ShopOffer(randomItem));
        }

        return new Shop(offers);
    }

    private List<Item> getAvailableItems(CharacterClass characterClass, int round) {
        return itemCatalog.getShopPool(characterClass, round);
    }
}
