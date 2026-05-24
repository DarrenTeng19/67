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

/**
 * 依照目前關卡產生敵人。
 *
 * 重點：Enemy 也要有背包道具，BattleEngine 才能讓敵人的道具自動觸發。
 */
public class EnemyFactory {
    private final ItemCatalog itemCatalog;

    public EnemyFactory(ItemCatalog itemCatalog) {
        if (itemCatalog == null) {
            throw new IllegalArgumentException("itemCatalog 不能是 null");
        }
        this.itemCatalog = itemCatalog;
    }

    public Enemy createEnemyForRound(int round) {
        int safeRound = Math.max(1, round);

        String name = "Enemy Round " + safeRound;
        CharacterStats stats = createStatsForRound(safeRound);
        BattleState battleState = new BattleState();
        Backpack backpack = new Backpack();

        equipEnemyForRound(backpack, safeRound);

        return new Enemy(name, stats, battleState, backpack);
    }

    private CharacterStats createStatsForRound(int round) {
        CharacterStats base = CharacterClass.WARRIOR.createInitialStats();

        // 先用簡單線性成長，之後你可以依照 HackMD 的敵人設計再調整。
        int bonusHp = (round - 1) * 20;
        int bonusStamina = Math.max(0, round - 2);
        int bonusMana = Math.max(0, round - 3);

        return new CharacterStats(
                base.getMaxHp() + bonusHp,
                base.getMaxStamina() + bonusStamina,
                base.getStaminaRecoveryRate(),
                base.getMaxMana() + bonusMana,
                base.getManaRecoveryRate()
        );
    }

    private void equipEnemyForRound(Backpack backpack, int round) {
        switch (round) {
            case 1 -> {
                place(backpack, "wooden_sword", 0, 0);
            }
            case 2 -> {
                place(backpack, "wooden_sword", 0, 0);
                place(backpack, "small_round_shield", 0, 2);
            }
            case 3 -> {
                place(backpack, "iron_sword", 0, 0);
                place(backpack, "small_round_shield", 0, 2);
                place(backpack, "banana", 3, 0);
            }
            case 4 -> {
                place(backpack, "iron_sword", 0, 0);
                place(backpack, "steel_shield", 0, 2);
                place(backpack, "hammer", 2, 0);
                place(backpack, "banana", 2, 2);
            }
            default -> {
                place(backpack, "iron_sword", 0, 0);
                place(backpack, "steel_shield", 0, 2);
                place(backpack, "big_hammer", 2, 0);
                place(backpack, "magic_stone", 2, 2);
                place(backpack, "banana", 2, 4);
            }
        }
    }

    private void place(Backpack backpack, String itemId, int row, int col) {
        Item item = itemCatalog.getById(itemId);
        PlacedItem placedItem = new PlacedItem(item, new GridPosition(row, col));

        boolean placed = backpack.tryPlaceItem(placedItem);
        if (!placed) {
            throw new IllegalStateException(
                    "敵人道具放置失敗: " + itemId + " at (" + row + ", " + col + ")"
            );
        }
    }
}
