package com.example._7.enemy;

import com.example._7.character.CharacterClass;
import com.example._7.character.Enemy;
import com.example._7.item.Item;
import com.example._7.item.ItemCatalog;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EnemyFactoryTest {
    private static final int[] WARRIOR_BASELINE_VALUES = {15, 27, 51, 66, 92};
    private static final int[] ALLOWED_VALUE_DIFFERENCE = {1, 1, 3, 2, 0};

    private final EnemyFactory enemyFactory = new EnemyFactory(new ItemCatalog());

    @Test
    void everyRoundHasFiveEnemiesCoveringAllClasses() {
        for (int round = 1; round <= 5; round++) {
            List<Enemy> pool = enemyFactory.createEnemyPoolForRound(round);
            EnumSet<CharacterClass> classes = EnumSet.noneOf(CharacterClass.class);
            pool.forEach(enemy -> classes.add(enemy.getCharacterClass()));

            assertEquals(5, pool.size());
            assertEquals(EnumSet.allOf(CharacterClass.class), classes);
        }
    }

    @Test
    void candidateValuesStayCloseToCurrentWarriorLoadout() {
        for (int round = 1; round <= 5; round++) {
            int baseline = WARRIOR_BASELINE_VALUES[round - 1];
            int allowedDifference = ALLOWED_VALUE_DIFFERENCE[round - 1];

            for (Enemy enemy : enemyFactory.createEnemyPoolForRound(round)) {
                int difference = Math.abs(loadoutValue(enemy) - baseline);
                assertTrue(
                        difference <= allowedDifference,
                        enemy.getName() + " value differs from baseline by " + difference
                );
            }
        }
    }

    @Test
    void randomSelectionUsesOneOfTheFiveRoundCandidates() {
        EnemyFactory selectingLast = new EnemyFactory(
                new ItemCatalog(),
                new FixedIndexRandom(4)
        );
        List<Enemy> pool = selectingLast.createEnemyPoolForRound(3);

        Enemy selected = selectingLast.createEnemyForRound(3);

        assertEquals(pool.get(4).getName(), selected.getName());
        assertEquals(pool.get(4).getCharacterClass(), selected.getCharacterClass());
        assertEquals(itemIds(pool.get(4)), itemIds(selected));
    }

    @Test
    void enemyBaseStatsMatchItsClass() {
        Enemy mage = enemyFactory.createEnemyPoolForRound(1).stream()
                .filter(enemy -> enemy.getCharacterClass() == CharacterClass.MAGE)
                .findFirst()
                .orElseThrow();

        assertEquals(95, mage.getCharacterStats().getMaxHp());
        assertEquals(3.0, mage.getCharacterStats().getManaRecoveryRate());
    }

    private int loadoutValue(Enemy enemy) {
        return enemy.getBackpack().getBattleItems().stream()
                .mapToInt(Item::getPrice)
                .sum();
    }

    private List<String> itemIds(Enemy enemy) {
        return enemy.getBackpack().getBattleItems().stream()
                .map(Item::getId)
                .toList();
    }

    private static class FixedIndexRandom extends Random {
        private final int index;

        private FixedIndexRandom(int index) {
            this.index = index;
        }

        @Override
        public int nextInt(int bound) {
            return Math.min(index, bound - 1);
        }
    }
}
