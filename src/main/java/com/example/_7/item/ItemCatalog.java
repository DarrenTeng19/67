package com.example._7.item;

/*
 * 負責提供道具列表
 * */

import com.example._7.character.CharacterClass;

import java.util.List;

public class ItemCatalog {
    private final List<Item> allItems;

    public ItemCatalog() {
        this.allItems = List.of(
                // 物品建立樣板
                /*createBanana(),
                createWoodenSword(),
                createShortBow(),
                createBroom(),
                createMagicStone(),
                createIronSword(),
                createDeadlyDagger(),
                createBurningBroom()*/
        );
    }

    public List<Item> getAllItems() {
        return allItems;
    }

    public List<Item> getShopPool(CharacterClass characterClass, int round) {
        return allItems.stream()
                .filter(item -> isAvailableForClass(item, characterClass))
                .filter(item -> isAvailableInRound(item, round))
                .toList();
    }

    ;

    private boolean isAvailableForClass(Item item, CharacterClass characterClass) {
        return item.getAffinity() == ItemAffinity.COMMON
                || item.getAffinity().matches(characterClass);
    }

    private boolean isAvailableInRound(Item item, int round) {
        // 第一版先簡單處理：
        // 目前 sample 沒有明確定義道具從第幾回合開始出現
        // 先全部可出現，之後再根據稀有度 / 關卡改
        return true;
    }

    // 物件清單
    /*
    * new Item(
        "deadly_dagger",
        "致命匕首",
        ItemAffinity.RANGER,
        Set.of(ItemRole.EQUIPMENT),
        ItemShapes.rectangle(1, 3),
        8,
        2,
        ItemTriggerType.ACTIVE,
        2.2,
        0.90,
        3,
        0,
        List.of(
                new DamageEffect(5),
                new PoisonEffect(1)
        )
);
    */
}

