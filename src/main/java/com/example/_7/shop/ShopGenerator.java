package com.example._7.shop;

/*
 * 關卡間商店內容物的生成
 */

import com.example._7.character.CharacterClass;
import com.example._7.item.Item;
import com.example._7.item.ItemAffinity;
import com.example._7.item.ItemCatalog;
import com.example._7.item.ItemRole;
import com.example._7.item.ItemTriggerType;
import com.example._7.item.effect.ItemEffect;
import com.example._7.item.shape.ItemShapes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * 關卡間商店內容物的生成
 */
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
        List<Item> pool = null;
        if (itemCatalog != null) {
            try {
                pool = itemCatalog.getShopPool(characterClass, round);
            } catch (Exception ignored) {
                // 如果 itemCatalog 尚未完全實作，忽略錯誤，走 fallback
            }
        }

        if (pool == null || pool.isEmpty()) {
            // fallback: 建立一個簡單的 placeholder item，避免空池
            return List.of(createPlaceholderItem());
        }

        return pool;
    }

    private Item createPlaceholderItem() {
        // Item 是 abstract，因此使用匿名子類別快速建立一個最簡單的 item
        return new Item(
                "placeholder_stick",
                "Wooden Stick",
                ItemAffinity.COMMON,
                Set.of(ItemRole.COMPONENT),
                ItemShapes.rectangle(1, 1),
                5, // price
                1, // rarity
                ItemTriggerType.PASSIVE,
                0.0, // cooldown
                1.0, // hitRate
                0,   // staminaCost
                0,   // manaCost
                "Fallback component item.",
                List.<ItemEffect>of()
        ) {};
    }
}