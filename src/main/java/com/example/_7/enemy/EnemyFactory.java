package com.example._7.enemy;

import com.example._7.battle.BattleState;
import com.example._7.character.CharacterClass;
import com.example._7.character.CharacterStats;
import com.example._7.character.Enemy;
import com.example._7.inventory.Backpack;
import com.example._7.inventory.GridPosition;
import com.example._7.inventory.PlacedItem;
import com.example._7.item.Item;
import com.example._7.item.ItemCatalog;

public class EnemyFactory {
    private final ItemCatalog itemCatalog;

    public EnemyFactory(ItemCatalog itemCatalog) {
        this.itemCatalog = itemCatalog;
    }

    public Enemy createEnemyForRound(int round) {
        String name = "Enemy Round " + round;
        CharacterStats stats = CharacterClass.WARRIOR.createInitialStats();
        BattleState battleState = new BattleState();
        Backpack backpack = new Backpack();

        addEnemyItems(backpack, round);

        return new Enemy(name, stats, battleState, backpack);
    }

    private void addEnemyItems(Backpack backpack, int round) {
        if (itemCatalog == null || backpack == null) {
            return;
        }

        if (round <= 1) {
            place(backpack, "wooden_sword", 0, 0);
            place(backpack, "banana", 0, 2);
        } else if (round == 2) {
            place(backpack, "wooden_sword", 0, 0);
            place(backpack, "small_round_shield", 0, 2);
        } else if (round == 3) {
            place(backpack, "iron_sword", 0, 0);
            place(backpack, "small_round_shield", 0, 2);
            place(backpack, "banana", 3, 0);
        } else if (round == 4) {
            place(backpack, "iron_sword", 0, 0);
            place(backpack, "steel_shield", 0, 2);
            place(backpack, "hammer", 3, 0);
        } else {
            place(backpack, "iron_sword", 0, 0);
            place(backpack, "steel_shield", 0, 2);
            place(backpack, "big_hammer", 2, 0);
            place(backpack, "magic_stone", 3, 3);
        }
    }

    private void place(Backpack backpack, String itemId, int row, int col) {
        try {
            Item item = itemCatalog.getById(itemId);
            backpack.tryPlaceItem(new PlacedItem(item, new GridPosition(row, col)));
        } catch (Exception ignored) {
            // 道具 id 不存在或位置放不下時，先略過，避免敵人生成失敗。
        }
    }
}
