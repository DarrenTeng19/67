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
            place(backpack, "small_round_shield", 0, 2);
            place(backpack, "hammer", 2, 0);
            place(backpack, "banana", 2, 2);
            place(backpack, "energy_drink", 2, 3);
        } else if (round == 2) {
            place(backpack, "iron_sword", 0, 0);
            place(backpack, "steel_shield", 0, 2);
            place(backpack, "hammer", 2, 0);
            place(backpack, "banana", 2, 2);
            place(backpack, "wooden_sword", 0, 4);
            place(backpack, "energy_drink", 2, 4);
        } else if (round == 3) {
            place(backpack, "big_hammer", 0, 0);
            place(backpack, "adaptive_helmet", 0, 2);
            place(backpack, "himmel_note", 2, 0);
            place(backpack, "banana", 2, 2);
            place(backpack, "energy_drink", 2, 3);
            place(backpack, "iron_sword", 0, 4);
            place(backpack, "iron_sword", 0, 1);
        } else if (round == 4) {
            place(backpack, "hero_himmel_greatsword", 0, 0);
            place(backpack, "thorn_armor", 0, 2);
            place(backpack, "himmel_note", 3, 0);
            place(backpack, "banana", 3, 2);
            place(backpack, "iron_sword", 3, 1);
            place(backpack, "energy_drink", 3, 3);
            place(backpack, "iron_sword", 0, 4);
        } else {
            place(backpack, "hero_himmel_greatsword", 0, 0);
            place(backpack, "hero_himmel_greatsword", 0, 1);
            place(backpack, "thorn_armor", 0, 2);
            place(backpack, "banana", 0, 4);
            place(backpack, "energy_drink", 2, 4);
            place(backpack, "himmel_note", 3, 0);
            place(backpack, "thorn_spread", 3, 1);
            place(backpack, "big_hammer", 3, 2);
        }
    }

    private void place(Backpack backpack, String itemId, int row, int col) {
        Item item = itemCatalog.getById(itemId);
        boolean placed = backpack.tryPlaceItem(new PlacedItem(item, new GridPosition(row, col)));
        if (!placed) {
            throw new IllegalStateException("Invalid enemy loadout placement for item: " + itemId);
        }
    }
}
