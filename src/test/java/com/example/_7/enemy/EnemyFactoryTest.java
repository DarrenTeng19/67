package com.example._7.enemy;

import com.example._7.character.Enemy;
import com.example._7.item.Item;
import com.example._7.item.ItemCatalog;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EnemyFactoryTest {
    private final EnemyFactory enemyFactory = new EnemyFactory(new ItemCatalog());

    @Test
    void fixedLoadoutsBecomeStrongerEveryRound() {
        int[] expectedValues = {15, 27, 51, 66, 92};
        int previousValue = 0;

        for (int round = 1; round <= 5; round++) {
            Enemy enemy = enemyFactory.createEnemyForRound(round);
            int value = loadoutValue(enemy);

            assertEquals(expectedValues[round - 1], value);
            assertTrue(value > previousValue);
            previousValue = value;
        }
    }

    @Test
    void finalRoundUsesTheFullHighTierLoadout() {
        Enemy enemy = enemyFactory.createEnemyForRound(5);

        assertEquals(
                List.of(
                        "hero_himmel_greatsword",
                        "hero_himmel_greatsword",
                        "thorn_armor",
                        "banana",
                        "energy_drink",
                        "himmel_note",
                        "thorn_spread",
                        "big_hammer"
                ),
                enemy.getBackpack().getBattleItems().stream()
                        .map(Item::getId)
                        .toList()
        );
        assertEquals(8, enemy.getBackpack().getPlacedItems().size());
    }

    private int loadoutValue(Enemy enemy) {
        return enemy.getBackpack().getBattleItems().stream()
                .mapToInt(Item::getPrice)
                .sum();
    }
}
